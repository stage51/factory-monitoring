package ru.centrikt.factorymonitoringservice.domain.models.fiveminute;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FiveMinuteReportRepository extends JpaRepository<FiveMinuteReport, Long> {
    Page<FiveMinuteReport> findAll(Specification<FiveMinuteReport> specification, Pageable pageable);
    @Query("SELECT d FROM FiveMinuteReport d " +
            "WHERE d.sensor.taxpayerNumber = :taxpayerNumber " +
            "AND d.createdAt = (SELECT MAX(d2.createdAt) FROM FiveMinuteReport d2 " +
            "WHERE d2.sensor.taxpayerNumber = :taxpayerNumber " +
            "AND d2.sensor.lineNumber = d.sensor.lineNumber " +
            "AND d2.sensor.controllerNumber = d.sensor.controllerNumber)")
    List<FiveMinuteReport> findLatestFiveMinuteReportsByTaxpayerNumber(@Param("taxpayerNumber") String taxpayerNumber);

}
