package space.banterbox.feature.authentication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import space.banterbox.feature.authentication.model.Jwt;
import space.banterbox.feature.user.model.User;

@Getter
@AllArgsConstructor
public class LoginResultDto {
    private Jwt accessToken;
    private Jwt refreshToken;
    private User user;
}
