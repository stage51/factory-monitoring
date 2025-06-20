package centrikt.factory_monitoring.daily_report.repos;

import centrikt.factory_monitoring.daily_report.models.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    Page<Position> findAll(Specification<Position> specification, Pageable pageable);
    @Query("SELECT p FROM Position p " +
            "WHERE p.taxpayerNumber = :taxpayerNumber " +
            "AND p.endDate = (SELECT MAX(p2.endDate) FROM Position p2 " +
            "WHERE p2.taxpayerNumber = :taxpayerNumber " +
            "AND p2.lineNumber = p.lineNumber " +
            "AND p2.controllerNumber = p.controllerNumber)")
    List<Position> findLatestPositionsByTaxpayerNumber(@Param("taxpayerNumber") String taxpayerNumber);

}
