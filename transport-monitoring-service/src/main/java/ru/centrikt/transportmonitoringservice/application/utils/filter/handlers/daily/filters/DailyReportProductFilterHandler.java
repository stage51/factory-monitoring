package ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.daily.filters;

import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.CustomFilterHandler;
import ru.centrikt.transportmonitoringservice.domain.models.daily.DailyPosition;
import ru.centrikt.transportmonitoringservice.domain.models.daily.DailyReport;

import java.util.List;

@Component
public class DailyReportProductFilterHandler implements CustomFilterHandler<DailyReport> {

    @Override
    public boolean supports(String fieldName) {
        return "product".equals(fieldName);
    }

    @Override
    public void applyFilter(Root<DailyReport> root, CriteriaBuilder cb, List<Predicate> predicates, String rawValue) {
        Join<DailyReport, DailyPosition> posJoin = root.join("positions", JoinType.LEFT);
        predicates.add(cb.like(
                cb.lower(posJoin.get("product").get("productVCode")),
                "%" + rawValue.toLowerCase() + "%"));
    }
}
