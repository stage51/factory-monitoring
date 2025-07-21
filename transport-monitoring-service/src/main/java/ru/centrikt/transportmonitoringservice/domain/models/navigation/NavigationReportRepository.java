package ru.centrikt.transportmonitoringservice.domain.models.navigation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NavigationReportRepository extends JpaRepository<NavigationReport, Long> {
    Page<NavigationReport> findAll(Specification<NavigationReport> specification, Pageable pageable);
    List<NavigationReport> findAll(Specification<NavigationReport> specification, Sort sort);
    Optional<NavigationReport> findFirstByDataNavigationDate(Specification<NavigationReport> specification, Sort sort);
}
