package space.banterbox.feature.authentication.api;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import space.banterbox.feature.authentication.config.JwtConfig;
import space.banterbox.feature.authentication.dto.request.LoginRequestDto;
import space.banterbox.feature.authentication.dto.response.JwtResponse;
import space.banterbox.feature.authentication.service.AuthService;
import space.banterbox.feature.user.dto.response.UserResponseDto;
import space.banterbox.feature.user.exception.UserNotFoundException;
import space.banterbox.feature.user.mapper.UserMapper;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequestDto request
    ) {
        var loginResult = authService.login(request);

        var refreshToken = loginResult.getRefreshToken().toString();
        var cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(jwtConfig.getRefreshTokenExpiration())
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(new JwtResponse(loginResult.getAccessToken().toString()));
    }

    @GetMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@CookieValue("refreshToken") String refreshToken) {
        var accessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me() {
        var user = authService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        System.out.println("here..2 ");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Void> handleUserNotFoundException() {
        System.out.println("here..");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}