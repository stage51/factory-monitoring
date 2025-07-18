package centrikt.factory_monitoring.config_server.utils.jwt;

import centrikt.factory_monitoring.config_server.exceptions.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Getter
@Component
@RefreshScope
public class JwtTokenUtil {


    private final SecretKey secretKey;

    private final SecretKey apiKey;

    public JwtTokenUtil(@Value("${security.access-token-secret-key}") String secret, @Value("${security.api-token-secret-key}") String apiKey) {
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
