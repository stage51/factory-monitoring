package ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.mode.dateranges;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.centrikt.transportmonitoringservice.application.configs.DateTimeConfig;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.CustomDateRangeHandler;
import ru.centrikt.transportmonitoringservice.domain.models.mode.ModeReport;
import ru.centrikt.transportmonitoringservice.presentation.dtos.extra.DateRange;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Component
public class ModeReportEndDateDateRangeHandler implements CustomDateRangeHandler<ModeReport> {

    private static final ZoneId USER_TIMEZONE = ZoneId.of(DateTimeConfig.getDefaultValue());
    
    @Override
    public boolean supports(String fieldName) {
        return "endDate".equals(fieldName);
    }

    @Override
    public void applyDateRange(Root<ModeReport> root, CriteriaBuilder cb, List<Predicate> predicates, String rawValue) {
        String[] dates = rawValue.split(",");
        if (dates.length != 2) {
            log.warn("Invalid date range '{}'", rawValue);
            return;
        }

        try {
            if (!"null".equals(dates[0])) {
                ZonedDateTime start = ZonedDateTime.parse(dates[0], DateRange.FORMATTER)
                        .withZoneSameInstant(USER_TIMEZONE);
                predicates.add(cb.greaterThanOrEqualTo(root.get("position").get("endDate"),
                        start.toLocalDateTime()));
            }
            if (!"null".equals(dates[1])) {
                ZonedDateTime end = ZonedDateTime.parse(dates[1], DateRange.FORMATTER)
                        .withZoneSameInstant(USER_TIMEZONE);
                predicates.add(cb.lessThanOrEqualTo(root.get("position").get("endDate"),
                        end.toLocalDateTime()));
            }
        } catch (DateTimeParseException ex) {
            log.error("Date parse error for '{}': {}", "endDate", ex.getMessage());
        }
    }
}
