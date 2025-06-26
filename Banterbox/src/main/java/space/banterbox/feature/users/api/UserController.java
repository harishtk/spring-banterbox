package space.banterbox.feature.users.api;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import space.banterbox.feature.users.dto.request.CreateUserRequestDto;
import space.banterbox.feature.users.dto.request.UpdatePasswordRequestDto;
import space.banterbox.feature.users.dto.request.UpdateUserRequestDto;
import space.banterbox.feature.users.dto.response.UserResponseDto;
import space.banterbox.feature.users.mapper.UserMapper;
import space.banterbox.feature.users.model.Role;
import space.banterbox.feature.users.service.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

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
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable("id") Long id) {
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
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateUserRequestDto request) {

        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userMapper.updateUser(request, user.get());
        return ResponseEntity.ok(userMapper.toDto(userRepository.save(user.orElseThrow())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userRepository.delete(user.orElseThrow());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable("id") Long id,
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
}
