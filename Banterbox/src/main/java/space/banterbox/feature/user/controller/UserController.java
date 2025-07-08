package space.banterbox.feature.user.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import space.banterbox.core.dto.ErrorDto;
import space.banterbox.core.response.StandardResponse;
import space.banterbox.feature.authentication.service.AuthService;
import space.banterbox.feature.user.dto.PagedUserPreviewDto;
import space.banterbox.feature.user.dto.PagedUserProfileDto;
import space.banterbox.feature.user.dto.request.UpdatePasswordRequestDto;
import space.banterbox.feature.user.dto.request.UpdateUserRequestDto;
import space.banterbox.feature.user.dto.response.UserProfileDto;
import space.banterbox.feature.user.dto.response.UserPreviewDto;
import space.banterbox.feature.user.exception.UserFollowException;
import space.banterbox.feature.user.exception.UserNotFoundException;
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
    public ResponseEntity<StandardResponse<PagedUserPreviewDto>> getUsers(
            @RequestParam(defaultValue = "name", required = false, name = "sort") String sortBy,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        final Set<String> supportedSortBy = Set.of("username");
        if (!supportedSortBy.contains(sortBy)) {
            sortBy = "username";
        }

        Page<UserPreviewDto> pagedData = userService.getAllUsers(sortBy, page, size);
        return ResponseEntity.ok(StandardResponse.success(getPagedUserPreviewResponse(pagedData)));
    }

    @Operation(summary = "Update user", description = "Update an existing user's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<UserProfileDto>> updateUser(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateUserRequestDto request) {

        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userMapper.updateUser(request, user.get());
        return ResponseEntity.ok(StandardResponse.success(userMapper.toDto(userRepository.save(user.orElseThrow()))));
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

    /* Profile */
    @Operation(summary = "Get current user profile", description = "Get the profile of the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/me")
    public ResponseEntity<StandardResponse<UserProfileDto>> getMyProfile() {
        var user = authService.getCurrentUser();
        return ResponseEntity.ok(StandardResponse.success(userService.enrichUserProfile(user, user)));
    }

    @Operation(summary = "Get user by @username", description = "Retrieve a user by their username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/@{username}")
    public ResponseEntity<StandardResponse<UserProfileDto>> getUserById(
            @AuthenticationPrincipal UUID viewerUserId,
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(StandardResponse.success(userService.getUserProfileByUsername(username, viewerUserId)));
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a user by their UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<StandardResponse<UserProfileDto>> getUserById(
            @AuthenticationPrincipal UUID viewerUserId,
            @PathVariable("userId") UUID userId
    ) {
        return ResponseEntity.ok(StandardResponse.success(userService.enrichUserProfile(userId, viewerUserId)));
    }
    /* END - Profile */

    /* User follow/followers */
    @Operation(summary = "Follow user", description = "Follow another user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully followed user"),
            @ApiResponse(responseCode = "403", description = "Cannot follow yourself")
    })
    @PostMapping("/{userId}/follow")
    public ResponseEntity<StandardResponse<UserProfileDto>> followUser(
            @AuthenticationPrincipal UUID currentUserId,
            @PathVariable("userId") UUID userId
    ) {
        if (Objects.equals(userId, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(StandardResponse.success(userService.follow(currentUserId, userId)));
    }

    @Operation(summary = "Unfollow user", description = "Unfollow a previously followed user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully unfollowed user"),
            @ApiResponse(responseCode = "403", description = "Cannot unfollow yourself")
    })
    @PostMapping("/{userId}/unfollow")
    public ResponseEntity<StandardResponse<UserProfileDto>> unfollowUser(
            @AuthenticationPrincipal UUID currentUserId,
            @PathVariable("userId") UUID userId
    ) {
        if (Objects.equals(userId, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(StandardResponse.success(userService.unfollow(currentUserId, userId)));
    }

    @Operation(summary = "Get followers", description = "Get a list of users who follow the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved followers list")
    })
    @GetMapping("/followers")
    public ResponseEntity<StandardResponse<PagedUserPreviewDto>> getFollowers(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        Page<UserPreviewDto> pagedData = userService.getFollowers(userId, page, size);
        return ResponseEntity.ok(StandardResponse.success(getPagedUserPreviewResponse(pagedData)));
    }

    @Operation(summary = "Get following", description = "Get a list of users that the current user follows")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved following list")
    })
    @GetMapping("/following")
    public ResponseEntity<StandardResponse<PagedUserPreviewDto>> getFollowing(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        Page<UserPreviewDto> pagedData = userService.getFollowing(userId, page, size);
        return ResponseEntity.ok(StandardResponse.success(getPagedUserPreviewResponse(pagedData)));
    }

    @Operation(summary = "Get followers of user", description = "Get a list of users who follow a given user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved followers list"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("{userId}/following")
    public ResponseEntity<StandardResponse<PagedUserPreviewDto>> getFollowingProfiles(
            @AuthenticationPrincipal UUID viewerUserId,
            @PathVariable("userId") UUID userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Page<UserPreviewDto> pagedData = userService.getFollowing(userId, page, size);
        return ResponseEntity.ok(StandardResponse.success(getPagedUserPreviewResponse(pagedData)));
    }

    @Operation(summary = "Get following of user", description = "Get a list of users that a given user follows")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved following list"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("{userId}/followers")
    public ResponseEntity<StandardResponse<PagedUserPreviewDto>> getFollowersProfiles(
            @AuthenticationPrincipal UUID viewerUserId,
            @PathVariable("userId") UUID userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Page<UserPreviewDto> pagedData = userService.getFollowers(userId, page, size);
        return ResponseEntity.ok(StandardResponse.success(getPagedUserPreviewResponse(pagedData)));
    }
    /* END - User follows/followers */

    @ExceptionHandler(UserFollowException.class)
    public ResponseEntity<StandardResponse<ErrorDto>> handleUserFollowException(UserFollowException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                StandardResponse.of(HttpStatus.CONFLICT.value(), "Conflict", new ErrorDto(exception.getMessage()))
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<StandardResponse<ErrorDto>> handleUserNotFoundException(UserNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                StandardResponse.of(HttpStatus.NOT_FOUND.value(), "Not found", new ErrorDto(exception.getMessage()))
        );
    }

    private PagedUserProfileDto getPagedUserProfileResponse(Page<UserProfileDto> pagedData) {
        return new PagedUserProfileDto(
                pagedData.getContent(),
                pagedData.getNumber(),
                pagedData.getSize(),
                pagedData.getTotalElements(),
                pagedData.getTotalPages(),
                pagedData.isLast()
        );
    }

    private PagedUserPreviewDto getPagedUserPreviewResponse(Page<UserPreviewDto> pagedData) {
        return new PagedUserPreviewDto(
                pagedData.getContent(),
                pagedData.getNumber(),
                pagedData.getSize(),
                pagedData.getTotalElements(),
                pagedData.getTotalPages(),
                pagedData.isLast()
        );
    }
}
