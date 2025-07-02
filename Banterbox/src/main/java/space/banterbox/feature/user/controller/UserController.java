package space.banterbox.feature.user.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import space.banterbox.core.dto.ErrorDto;
import space.banterbox.core.response.PagedResponse;
import space.banterbox.feature.authentication.service.AuthService;
import space.banterbox.feature.user.dto.request.UpdatePasswordRequestDto;
import space.banterbox.feature.user.dto.request.UpdateUserRequestDto;
import space.banterbox.feature.user.dto.response.UserProfileDto;
import space.banterbox.feature.user.dto.response.UserPreviewDto;
import space.banterbox.feature.user.dto.response.UserResponseDto;
import space.banterbox.feature.user.exception.ProfileNotFoundException;
import space.banterbox.feature.user.mapper.UserMapper;
import space.banterbox.feature.user.repository.UserRepository;
import space.banterbox.feature.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.*;

@Tag(name = "Users", description = "User management APIs")
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;
    private final AuthService authService;

    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users")
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsers(
            @RequestParam(defaultValue = "name", required = false, name = "sort") String sortBy
    ) {
        final Set<String> supportedSortBy = Set.of("username");
        if (!supportedSortBy.contains(sortBy)) {
            sortBy = "username";
        }

        return ResponseEntity.ok(
                userRepository.findAll(Sort.by(sortBy))
                        .stream().map(userMapper::toDto)
                        .toList()
        );
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a user by their UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable("id") UUID id) {
        var user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(userMapper.toDto(user.orElseThrow()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update user", description = "Update an existing user's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateUserRequestDto request) {

        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userMapper.updateUser(request, user.get());
        return ResponseEntity.ok(userMapper.toDto(userRepository.save(user.orElseThrow())));
    }

    @Operation(summary = "Delete user", description = "Delete an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID id) {
        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userRepository.delete(user.orElseThrow());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change password", description = "Change user's password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password successfully changed"),
            @ApiResponse(responseCode = "401", description = "Invalid old password"),
            @ApiResponse(responseCode = "412", description = "New passwords don't match")
    })
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdatePasswordRequestDto request) {
        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Validate password
        if (request.getOldPassword() == null || !request.getOldPassword().equals(user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (!Objects.equals(request.getNewPassword(), request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }

        user.get().setPassword(request.getConfirmPassword());
        userRepository.save(user.orElseThrow());
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Validate username", description = "Check if a username is available for registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username validation successful"),
            @ApiResponse(responseCode = "400", description = "Invalid username provided")
    })
    @GetMapping("/validate-username")
    public ResponseEntity<Map<String, Boolean>> validateUsername(@RequestParam("username") String username) {
        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("valid", false));
        }
        
        if (userRepository.existsUsersByUsername(username)) {
            return ResponseEntity.ok(Map.of("valid", false));
        }
        
        return ResponseEntity.ok(Map.of("valid", true));
    }

    /* Profile */
    @Operation(summary = "Get current user profile", description = "Get the profile of the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getMyProfile() {
        var user = authService.getCurrentUser();

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userService.getProfile(user.getId()));
    }
    /* END - Profile */

    /* User follow/followers */
    @Operation(summary = "Follow user", description = "Follow another user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully followed user"),
            @ApiResponse(responseCode = "403", description = "Cannot follow yourself")
    })
    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> followUser(@PathVariable("id") UUID userId) {
        var currentUser = authService.getCurrentUser();

        if (Objects.equals(userId, currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.follow(currentUser.getId(), userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Unfollow user", description = "Unfollow a previously followed user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully unfollowed user"),
            @ApiResponse(responseCode = "403", description = "Cannot unfollow yourself")
    })
    @PostMapping("/{id}/unfollow")
    public ResponseEntity<Void> unfollowUser(@PathVariable("id") UUID userId) {
        var currentUser = authService.getCurrentUser();

        if (Objects.equals(userId, currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.unfollow(currentUser.getId(), userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get followers", description = "Get a list of users who follow the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved followers list")
    })
    @GetMapping("/followers")
    public ResponseEntity<PagedResponse<UserPreviewDto>> getFollowers(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        Page<UserPreviewDto> pagedData = userService.getFollowers(userId, page, size);

        var response = new PagedResponse<>(
                pagedData.getContent(),
                pagedData.getNumber(),
                pagedData.getSize(),
                pagedData.getTotalElements(),
                pagedData.getTotalPages(),
                pagedData.isLast()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get following", description = "Get a list of users that the current user follows")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved following list")
    })
    @GetMapping("/following")
    public ResponseEntity<PagedResponse<UserPreviewDto>> getFollowing(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        Page<UserPreviewDto> pagedData = userService.getFollowing(userId, page, size);

        var response = new PagedResponse<>(
                pagedData.getContent(),
                pagedData.getNumber(),
                pagedData.getSize(),
                pagedData.getTotalElements(),
                pagedData.getTotalPages(),
                pagedData.isLast()
        );

        return ResponseEntity.ok(response);
    }
    /* END - User follows/followers */

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ErrorDto> handleProfileNotFoundException(ProfileNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorDto(exception.getMessage())
        );
    }
}
