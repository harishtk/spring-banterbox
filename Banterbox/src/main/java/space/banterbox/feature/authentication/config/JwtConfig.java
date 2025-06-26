package space.banterbox.feature.authentication.config;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Getter
@Setter
@Configuration
public class JwtConfig {
    @Value("${spring.jwt.secret}")
    private String secret;
    @Value("${spring.jwt.accessTokenExpiration}")
    private Long accessTokenExpiration;
    @Value("${spring.jwt.refreshTokenExpiration}")
    private Long refreshTokenExpiration;

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
