package ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.daily.filters;

import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.CustomFilterHandler;
import ru.centrikt.factorymonitoringservice.domain.enums.Status;
import ru.centrikt.factorymonitoringservice.domain.models.daily.DailyReport;

import java.util.List;

@Component
public class DailyReportStatusFilterHandler implements CustomFilterHandler<DailyReport> {
    @Override
    public boolean supports(String fieldName) {
        return "status".equals(fieldName);
    }

    @Override
    public void applyFilter(Root<DailyReport> root, CriteriaBuilder cb, List<Predicate> predicates, String rawValue) {
        Status status = Status.fromDescription(rawValue);
        predicates.add(cb.equal(root.get("status"), status));
    }
}
