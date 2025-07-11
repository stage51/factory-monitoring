package ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.fiveminute.filters;

import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.CustomFilterHandler;
import ru.centrikt.factorymonitoringservice.domain.models.fiveminute.FiveMinuteReport;

import java.util.List;

@Component
public class FiveMinuteReportProductFilterHandler implements CustomFilterHandler<FiveMinuteReport> {

    @Override
    public boolean supports(String fieldName) {
        return "product".equals(fieldName);
    }

    @Override
    public void applyFilter(Root<FiveMinuteReport> root, CriteriaBuilder cb, List<Predicate> predicates, String rawValue) {
        predicates.add(cb.like(
                cb.lower(root.get("position").get("product").get("productVCode")),
                "%" + rawValue.toLowerCase() + "%"));
    }
}
