package space.banterbox.feature.authentication.service;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import space.banterbox.feature.authentication.dto.request.LoginRequestDto;
import space.banterbox.feature.authentication.dto.request.SignupRequestDto;
import space.banterbox.feature.authentication.dto.response.LoginResultDto;
import space.banterbox.feature.authentication.model.Jwt;
import space.banterbox.feature.user.exception.UsernameExistsException;
import space.banterbox.feature.user.model.Role;
import space.banterbox.feature.user.model.User;
import space.banterbox.feature.user.repository.UserRepository;

import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public User getCurrentUser() {
        // Get user object from auth
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var userId = (UUID) auth.getPrincipal();

        return userRepository.findById(userId).orElse(null);
    }

    public LoginResultDto login(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = userRepository.findUserByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        final var accessToken = jwtService.generateAccessToken(user);
        final var refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResultDto(accessToken, refreshToken, user);
    }

    public User signup(SignupRequestDto request) {
        if (userRepository.existsUsersByUsername(request.getUsername())) {
            throw new UsernameExistsException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setDisplayName(request.getDisplayName());
        user.setBio(request.getBio());
        return userRepository.save(user);
    }

    public Jwt refreshAccessToken(String refreshToken) {
        var jwt = jwtService.parse(refreshToken);
        if (jwt == null || jwt.isExpired()) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        var user = userRepository.findById(jwt.getUserId()).orElseThrow();
        return jwtService.generateAccessToken(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsUsersByUsername(username);
    }
}
