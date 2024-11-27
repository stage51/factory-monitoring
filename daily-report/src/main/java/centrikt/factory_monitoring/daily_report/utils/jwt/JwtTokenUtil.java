package centrikt.factory_monitoring.daily_report.utils.jwt;

import centrikt.factory_monitoring.daily_report.exceptions.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Getter
@Component
public class JwtTokenUtil {


    private final SecretKey secretKey;

    private final SecretKey apiKey;

    public JwtTokenUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.api-secret}") String apiKey) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.apiKey = Keys.hmacShaKeyFor(apiKey.getBytes());
    }

    public Claims validateAndExtractClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid or expired access token");
        }
    }

    public String extractUsername(String token) {
        return validateAndExtractClaims(token).getSubject();
    }

    public String extractUserRole(String token) {
        return validateAndExtractClaims(token).get("role", String.class);
    }

    public Claims validateApiTokenAndExtractClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(apiKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid api token");
        }
    }

    public String extractApiUsername(String token) {
        return validateApiTokenAndExtractClaims(token).getSubject();
    }

    public String extractApiUserRole(String token) {
        return validateApiTokenAndExtractClaims(token).get("role", String.class);
    }
}
