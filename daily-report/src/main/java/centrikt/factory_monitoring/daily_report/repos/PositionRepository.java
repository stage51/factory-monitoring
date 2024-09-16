package centrikt.factory_monitoring.daily_report.repos;

import centrikt.factory_monitoring.daily_report.models.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
}
