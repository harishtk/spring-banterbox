package space.banterbox.feature.authentication.service;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import space.banterbox.feature.authentication.dto.request.LoginRequestDto;
import space.banterbox.feature.authentication.dto.response.LoginResponseDto;
import space.banterbox.feature.authentication.model.Jwt;
import space.banterbox.feature.user.model.User;
import space.banterbox.feature.user.repository.UserRepository;

import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public User getCurrentUser() {
        // Get user object from auth
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var userId = (UUID) auth.getPrincipal();

        return userRepository.findById(userId).orElse(null);
    }

    public LoginResponseDto login(LoginRequestDto request) {
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

        return new LoginResponseDto(accessToken, refreshToken);
    }

    public Jwt refreshAccessToken(String refreshToken) {
        var jwt = jwtService.parse(refreshToken);
        if (jwt == null || jwt.isExpired()) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        var user = userRepository.findById(jwt.getUserId()).orElseThrow();
        return jwtService.generateAccessToken(user);
    }
}
