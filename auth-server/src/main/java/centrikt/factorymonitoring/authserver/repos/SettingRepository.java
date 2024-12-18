package centrikt.factorymonitoring.authserver.repos;

import centrikt.factorymonitoring.authserver.models.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
}
