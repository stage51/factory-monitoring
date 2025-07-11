package ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.fiveminute.filters;

import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.CustomFilterHandler;
import ru.centrikt.factorymonitoringservice.domain.enums.Mode;
import ru.centrikt.factorymonitoringservice.domain.models.fiveminute.FiveMinuteReport;

import java.util.List;

@Component
public class FiveMinuteReportModeFilterHandler implements CustomFilterHandler<FiveMinuteReport> {
    @Override
    public boolean supports(String fieldName) {
        return "mode".equals(fieldName);
    }

    @Override
    public void applyFilter(Root<FiveMinuteReport> root, CriteriaBuilder cb, List<Predicate> predicates, String rawValue) {
        Mode mode = Mode.fromCodeOrDescription(rawValue);
        predicates.add(cb.equal(
                root.get("position").get("mode"),
                mode));
    }
}
