package ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.daily.filters;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.CustomFilterHandler;
import ru.centrikt.factorymonitoringservice.domain.models.daily.DailyReport;

import java.util.List;

@Slf4j
@Component
public class DailyReportSensorNumberFilterHandler implements CustomFilterHandler<DailyReport> {

    @Override
    public boolean supports(String fieldName) {
        return "sensorNumber".equals(fieldName);
    }

    @Override
    public void applyFilter(Root<DailyReport> root, CriteriaBuilder cb, List<Predicate> predicates, String rawValue) {
        if (rawValue.matches("\\d+_\\d+")) {
            String controllerNumber = rawValue.split("_")[0];
            String lineNumber = rawValue.split("_")[1];
            predicates.add(cb.equal(root.get("sensor").get("controllerNumber"), controllerNumber));
            predicates.add(cb.equal(root.get("sensor").get("lineNumber"), lineNumber));
        } else {
            log.warn("Invalid sensorNumber format: {}", rawValue);
        }
    }
}
