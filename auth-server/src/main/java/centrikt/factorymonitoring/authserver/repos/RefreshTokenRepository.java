package centrikt.factorymonitoring.authserver.repos;

import centrikt.factorymonitoring.authserver.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenAndUserEmail(String token, String userEmail);
    boolean existsByToken(String token);
    void deleteByToken(String token);
}

