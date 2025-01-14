package centrikt.factorymonitoring.authserver.utils.jwt;

import centrikt.factorymonitoring.authserver.exceptions.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Slf4j
@Getter
@Component
@RefreshScope
public class JwtTokenUtil {

    private final SecretKey secretKey;
    private final SecretKey apiKey;

    public JwtTokenUtil(@Value("${security.access-token-secret-key}") String secret,
                        @Value("${security.api-token-secret-key}") String apiKey) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.apiKey = Keys.hmacShaKeyFor(apiKey.getBytes());
        log.trace("JwtTokenUtil initialized with provided keys");
    }

    public Claims validateAndExtractClaims(String token) {
        log.trace("Validating and extracting claims for token: {}", token);
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.debug("Error validating access token: {}", e.getMessage(), e);
            throw new InvalidTokenException("Invalid or expired access token");
        }
    }

    public String extractUsername(String token) {
        log.trace("Extracting username from token: {}", token);
        return validateAndExtractClaims(token).getSubject();
    }

    public String extractUserRole(String token) {
        log.trace("Extracting user role from token: {}", token);
        return validateAndExtractClaims(token).get("role", String.class);
    }

    public Claims validateApiTokenAndExtractClaims(String token) {
        log.trace("Validating and extracting claims for API token: {}", token);
        try {
            return Jwts.parser()
                    .setSigningKey(apiKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.debug("Error validating API token: {}", e.getMessage(), e);
            throw new InvalidTokenException("Invalid API token");
        }
    }

    public String extractApiUsername(String token) {
        log.trace("Extracting API username from token: {}", token);
        return validateApiTokenAndExtractClaims(token).getSubject();
    }

    public String extractApiUserRole(String token) {
        log.trace("Extracting API user role from token: {}", token);
        return validateApiTokenAndExtractClaims(token).get("role", String.class);
    }
}
