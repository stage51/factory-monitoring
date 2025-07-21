package ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.mode.filters;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.CustomFilterHandler;
import ru.centrikt.factorymonitoringservice.domain.enums.Type;
import ru.centrikt.factorymonitoringservice.domain.models.mode.ModeReport;

import java.util.List;

@Component
public class ModeReportTaxpayerNumberFilterHandler implements CustomFilterHandler<ModeReport> {
    @Override
    public boolean supports(String fieldName) {
        return "taxpayerNumber".equals(fieldName);
    }

    @Override
    public void applyFilter(Root<ModeReport> root, CriteriaBuilder cb, List<Predicate> predicates, String rawValue) {
        predicates.add(cb.equal(root.get("sensor").get("taxpayerNumber"), rawValue));
    }
}
