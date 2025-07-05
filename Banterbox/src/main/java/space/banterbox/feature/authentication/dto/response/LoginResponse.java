package space.banterbox.feature.authentication.dto.response;

import space.banterbox.feature.user.dto.response.UserProfileDto;

public record LoginResponse(String accessToken, String refreshToken, UserProfileDto loginUser) {
}
