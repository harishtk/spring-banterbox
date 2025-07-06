package space.banterbox.feature.authentication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import space.banterbox.core.response.StandardResponse;
import space.banterbox.feature.authentication.config.JwtConfig;
import space.banterbox.feature.authentication.dto.request.LoginRequestDto;
import space.banterbox.feature.authentication.dto.request.SignupRequestDto;
import space.banterbox.feature.authentication.dto.response.JwtResponse;
import space.banterbox.feature.authentication.dto.response.LoginResponse;
import space.banterbox.feature.authentication.service.AuthService;
import space.banterbox.feature.user.dto.response.UserProfileDto;
import space.banterbox.feature.user.exception.UserNotFoundException;
import space.banterbox.feature.user.exception.UsernameExistsException;
import space.banterbox.feature.user.mapper.UserMapper;
import space.banterbox.feature.user.service.UserService;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

@Tag(name = "Authentication", description = "Authentication management APIs")
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;
    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "Login user", description = "Authenticate user and return JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<StandardResponse<LoginResponse>> login(
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

        var userId = loginResult.getUser().getId();
        var loginUser = userService.enrichUserProfile(userId, userId);

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(StandardResponse.success(new LoginResponse(
                        loginResult.getAccessToken().toString(),
                        refreshToken,
                        loginUser)));
    }

    @Operation(summary = "Create new user", description = "Create a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created"),
            @ApiResponse(responseCode = "428", description = "Username already exists")
    })
    @PostMapping("signup")
    public ResponseEntity<?> signup(
            @Valid @RequestBody SignupRequestDto request) {

        var savedUser = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                StandardResponse.success(HttpStatus.CREATED, userMapper.toDto(savedUser))
        );
    }

    
    @Operation(summary = "Validate username", description = "Check if a username is available for registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username validation successful"),
            @ApiResponse(responseCode = "400", description = "Invalid username provided")
    })
    @GetMapping("/validate-username")
    public ResponseEntity<StandardResponse<Map<String, Boolean>>> validateUsername(
            @NotBlank(message = "Username cannot be empty")
            @RequestParam("username") String username) {

        if (authService.existsByUsername(username)) {
            return ResponseEntity.ok(StandardResponse.success(Map.of("available", false)));
        }

        return ResponseEntity.ok(StandardResponse.success(Map.of("available", true)));
    }

    @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New access token generated"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    @GetMapping("/refresh")
    public ResponseEntity<StandardResponse<JwtResponse>> refresh(@CookieValue("refreshToken") String refreshToken) {
        var accessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(StandardResponse.success(new JwtResponse(accessToken.toString(), refreshToken)));
    }

    @Operation(summary = "Get current user", description = "Get details of currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/me")
    public ResponseEntity<StandardResponse<UserProfileDto>> me() {
        var user = authService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(StandardResponse.success(userMapper.toDto(user)));
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

    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<Void> handleUsernameExistsException() {
        System.out.println("here..1 ");
        return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).build();
    }
}