package centrikt.factorymonitoring.authserver.utils;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "your-secret-key";
    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 15; // 15 минут
    private final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24; // 1 день

    public String generateToken(String email, Map<String, Object> claims, boolean isRefreshToken) {
        long validity = isRefreshToken ? REFRESH_TOKEN_VALIDITY : ACCESS_TOKEN_VALIDITY;

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()

                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}

