package ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.navigation.filters;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.CustomFilterHandler;
import ru.centrikt.transportmonitoringservice.domain.models.navigation.NavigationReport;

import java.util.List;

@Component
public class NavigationReportGovNumberFilterHandler implements CustomFilterHandler<NavigationReport> {
    @Override
    public boolean supports(String fieldName) {
        return "govNumber".equals(fieldName);
    }

    @Override
    public void applyFilter(Root<NavigationReport> root, CriteriaBuilder cb, List<Predicate> predicates, String rawValue) {
        predicates.add(cb.equal(root.get("sensor").get("govNumber"), rawValue));
    }
}
