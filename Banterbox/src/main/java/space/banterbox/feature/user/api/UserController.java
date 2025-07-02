package space.banterbox.feature.user.api;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import space.banterbox.core.response.PagedResponse;
import space.banterbox.feature.authentication.service.AuthService;
import space.banterbox.feature.user.dto.request.CreateUserRequestDto;
import space.banterbox.feature.user.dto.request.UpdatePasswordRequestDto;
import space.banterbox.feature.user.dto.request.UpdateUserRequestDto;
import space.banterbox.feature.user.dto.response.ProfileDto;
import space.banterbox.feature.user.dto.response.UserPreviewDto;
import space.banterbox.feature.user.dto.response.UserResponseDto;
import space.banterbox.feature.user.mapper.UserMapper;
import space.banterbox.feature.user.model.Role;
import space.banterbox.feature.user.repository.UserRepository;
import space.banterbox.feature.user.service.UserService;

import java.util.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsers(
            @RequestParam(defaultValue = "name", required = false, name = "sort") String sortBy
    ) {
        final Set<String> supportedSortBy = Set.of("name", "email");
        if (!supportedSortBy.contains(sortBy)) {
            sortBy = "name";
        }

        return ResponseEntity.ok(
                userRepository.findAll(Sort.by(sortBy))
                        .stream().map(userMapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable("id") UUID id) {
        var user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(userMapper.toDto(user.orElseThrow()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(
            @Valid @RequestBody CreateUserRequestDto request,
            UriComponentsBuilder uriBuilder) {

        if (userRepository.existsUsersByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(
                    Map.of("error", "Email is already in use!")
            );
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        user = userRepository.save(user);
        var location = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).body(userMapper.toDto(user));
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID id) {
        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userRepository.delete(user.orElseThrow());
        return ResponseEntity.noContent().build();
    }

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
    @GetMapping("/me")
    public ResponseEntity<ProfileDto> getMyProfile() {
        var user = authService.getCurrentUser();

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userService.getProfile(user.getId()));
    }
    /* END - Profile */

    /* User follow/followers */
    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> followUser(@PathVariable("id") UUID userId) {
        var currentUser = authService.getCurrentUser();

        if (Objects.equals(userId, currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.follow(currentUser.getId(), userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/unfollow")
    public ResponseEntity<Void> unfollowUser(@PathVariable("id") UUID userId) {
        var currentUser = authService.getCurrentUser();

        if (Objects.equals(userId, currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.unfollow(currentUser.getId(), userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<PagedResponse<UserPreviewDto>> getFollowers(
            @PathVariable("id") UUID userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        var currentUser = authService.getCurrentUser();

        if (Objects.equals(userId, currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

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

    @GetMapping("/{id}/following")
    public ResponseEntity<PagedResponse<UserPreviewDto>> getFollowing(
            @PathVariable("id") UUID userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        var currentUser = authService.getCurrentUser();

        if (Objects.equals(userId, currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Page<UserPreviewDto> pagedData = getUserServiceFollowing(userId, page, size);

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

    private Page<UserPreviewDto> getUserServiceFollowing(UUID userId, int page, int size) {
        return userService.getFollowing(userId, page, size);
    }
    /* END - User follows/followers */

}
