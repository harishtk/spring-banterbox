package space.banterbox.feature.authentication.dto.response;

public record JwtResponse(
        String accessToken,
        String refreshToken) {}
