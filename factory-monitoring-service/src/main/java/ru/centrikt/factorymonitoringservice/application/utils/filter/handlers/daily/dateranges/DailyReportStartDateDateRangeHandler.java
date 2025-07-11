package ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.daily.dateranges;

import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.centrikt.factorymonitoringservice.application.configs.DateTimeConfig;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.CustomDateRangeHandler;
import ru.centrikt.factorymonitoringservice.domain.models.daily.DailyReport;
import ru.centrikt.factorymonitoringservice.domain.models.daily.DailyPosition;
import ru.centrikt.factorymonitoringservice.presentation.dtos.extra.DateRange;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Component
public class DailyReportStartDateDateRangeHandler implements CustomDateRangeHandler<DailyReport> {

    private static final ZoneId USER_TIMEZONE = ZoneId.of(DateTimeConfig.getDefaultValue());

    @Override
    public boolean supports(String fieldName) {
        return "startDate".equals(fieldName);
    }

    @Override
    public void applyDateRange(Root<DailyReport> root, CriteriaBuilder cb, List<Predicate> predicates, String rawValue) {
        Join<DailyReport, DailyPosition> posJoin = root.join("positions", JoinType.LEFT);
        String[] dates = rawValue.split(",");
        if (dates.length != 2) {
            log.warn("Invalid date range '{}'", rawValue);
            return;
        }

        try {
            if (!"null".equals(dates[0])) {
                ZonedDateTime start = ZonedDateTime.parse(dates[0], DateRange.FORMATTER)
                        .withZoneSameInstant(USER_TIMEZONE);
                predicates.add(cb.greaterThanOrEqualTo(posJoin.get("startDate"),
                        start.toLocalDateTime()));
            }
            if (!"null".equals(dates[1])) {
                ZonedDateTime end = ZonedDateTime.parse(dates[1], DateRange.FORMATTER)
                        .withZoneSameInstant(USER_TIMEZONE);
                predicates.add(cb.lessThanOrEqualTo(posJoin.get("startDate"),
                        end.toLocalDateTime()));
            }
        } catch (DateTimeParseException ex) {
            log.error("Date parse error for '{}': {}", "startDate", ex.getMessage());
        }
    }
}
