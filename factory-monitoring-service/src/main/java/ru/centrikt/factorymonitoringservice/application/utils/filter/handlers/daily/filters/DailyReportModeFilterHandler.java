package ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.daily.filters;

import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.CustomFilterHandler;
import ru.centrikt.factorymonitoringservice.domain.enums.Mode;
import ru.centrikt.factorymonitoringservice.domain.models.daily.DailyReport;
import ru.centrikt.factorymonitoringservice.domain.models.daily.DailyPosition;

import java.util.List;

@Component
public class DailyReportModeFilterHandler implements CustomFilterHandler<DailyReport> {
    @Override
    public boolean supports(String fieldName) {
        return "mode".equals(fieldName);
    }

    @Override
    public void applyFilter(Root<DailyReport> root, CriteriaBuilder cb, List<Predicate> predicates, String rawValue) {
        Mode mode = Mode.fromCodeOrDescription(rawValue);
        Join<DailyReport, DailyPosition> posJoin = root.join("positions", JoinType.LEFT);
        predicates.add(cb.equal(
                posJoin.get("mode"),
                mode));
    }
}
