package centrikt.factorymonitoring.authserver.repos;

import centrikt.factorymonitoring.authserver.models.Recovery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecoveryRepository extends JpaRepository<Recovery, Long> {
    Optional<Recovery> findByCode(String code);
}
