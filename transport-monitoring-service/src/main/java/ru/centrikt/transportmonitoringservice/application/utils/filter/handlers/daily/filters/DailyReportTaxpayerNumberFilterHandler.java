package ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.daily.filters;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.CustomFilterHandler;
import ru.centrikt.transportmonitoringservice.domain.models.daily.DailyReport;

import java.util.List;

@Component
public class DailyReportTaxpayerNumberFilterHandler implements CustomFilterHandler<DailyReport> {
    @Override
    public boolean supports(String fieldName) {
        return "taxpayerNumber".equals(fieldName);
    }

    @Override
    public void applyFilter(Root<DailyReport> root, CriteriaBuilder cb, List<Predicate> predicates, String rawValue) {
        predicates.add(cb.equal(root.get("sensor").get("taxpayerNumber"), rawValue));
    }
}
