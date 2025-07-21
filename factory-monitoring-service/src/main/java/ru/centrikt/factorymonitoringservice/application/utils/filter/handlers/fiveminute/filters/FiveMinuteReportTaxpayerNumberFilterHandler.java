package ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.fiveminute.filters;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.CustomFilterHandler;
import ru.centrikt.factorymonitoringservice.domain.enums.Status;
import ru.centrikt.factorymonitoringservice.domain.models.fiveminute.FiveMinuteReport;

import java.util.List;

@Component
public class FiveMinuteReportTaxpayerNumberFilterHandler implements CustomFilterHandler<FiveMinuteReport> {
    @Override
    public boolean supports(String fieldName) {
        return "taxpayerNumber".equals(fieldName);
    }

    @Override
    public void applyFilter(Root<FiveMinuteReport> root, CriteriaBuilder cb, List<Predicate> predicates, String rawValue) {
        predicates.add(cb.equal(root.get("sensor").get("taxpayerNumber"), rawValue));
    }
}
