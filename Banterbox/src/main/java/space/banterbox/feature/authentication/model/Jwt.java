package space.banterbox.feature.authentication.model;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import space.banterbox.feature.user.model.Role;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

public class Jwt {
    private final Claims claims;
    private final SecretKey key;

    public Jwt(Claims claims, SecretKey key) {
        this.claims = claims;
        this.key = key;
    }

    public boolean isExpired() {
        try { return claims.getExpiration().before(new Date()); }
        catch (JwtException e) { return false; }
    }

    public UUID getUserId() {
        return UUID.fromString(claims.getSubject());
    }

    public Role getUserRole() {
        return Role.valueOf(claims.get("role", String.class));
    }

    public String toString() {
        return Jwts.builder().claims(claims).signWith(key).compact();
    }

}
