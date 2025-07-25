package ru.centrikt.transportmonitoringservice.domain.models.mode;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ModeReportRepository extends JpaRepository<ModeReport, Long> {
    Page<ModeReport> findAll(Specification<ModeReport> specification, Pageable pageable);
}
