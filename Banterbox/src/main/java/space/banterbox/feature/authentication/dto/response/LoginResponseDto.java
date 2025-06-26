package space.banterbox.feature.authentication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import space.banterbox.feature.authentication.model.Jwt;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private Jwt accessToken;
    private Jwt refreshToken;
}
