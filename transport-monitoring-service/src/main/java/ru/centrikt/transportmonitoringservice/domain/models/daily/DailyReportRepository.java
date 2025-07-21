package ru.centrikt.transportmonitoringservice.domain.models.daily;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
    Page<DailyReport> findAll(Specification<DailyReport> specification, Pageable pageable);
    @Query("SELECT d FROM DailyReport d " +
            "WHERE d.sensor.taxpayerNumber = :taxpayerNumber " +
            "AND d.createdAt = (SELECT MAX(d2.createdAt) FROM DailyReport d2 " +
            "WHERE d2.sensor.taxpayerNumber = :taxpayerNumber)")
    List<DailyReport> findLatestDailyReportsByTaxpayerNumber(@Param("taxpayerNumber") String taxpayerNumber);

}