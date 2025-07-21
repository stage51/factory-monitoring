package ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.mode.filters;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.CustomFilterHandler;
import ru.centrikt.transportmonitoringservice.domain.enums.UnitType;
import ru.centrikt.transportmonitoringservice.domain.models.mode.ModeReport;

import java.util.List;

@Component
public class ModeReportUnitTypeFilterHandler implements CustomFilterHandler<ModeReport> {
    @Override
    public boolean supports(String fieldName) {
        return "unitType".equals(fieldName);
    }

    @Override
    public void applyFilter(Root<ModeReport> root, CriteriaBuilder cb, List<Predicate> predicates, String rawValue) {
        UnitType type = UnitType.fromString(rawValue);
        predicates.add(cb.equal(root.get("position").get("product").get("unitType"), type));
    }
}
