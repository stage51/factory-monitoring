package ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.mode.filters;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.CustomFilterHandler;
import ru.centrikt.factorymonitoringservice.domain.models.mode.ModeReport;

import java.util.List;

@Component
public class ModeReportProductFilterHandler implements CustomFilterHandler<ModeReport> {

    @Override
    public boolean supports(String fieldName) {
        return "product".equals(fieldName);
    }

    @Override
    public void applyFilter(Root<ModeReport> root, CriteriaBuilder cb, List<Predicate> predicates, String rawValue) {
        predicates.add(cb.like(
                cb.lower(root.get("position").get("product").get("productVCode")),
                "%" + rawValue.toLowerCase() + "%"));
    }
}
