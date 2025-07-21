package ru.centrikt.transportmonitoringservice.application.utils.filter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.centrikt.transportmonitoringservice.application.configs.DateTimeConfig;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.CustomDateRangeHandler;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.CustomFilterHandler;
import ru.centrikt.transportmonitoringservice.presentation.dtos.extra.DateRange;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class FilterUtilImpl<Entity> implements FilterUtil<Entity> {
    private static final ZoneId USER_TIMEZONE = ZoneId.of(DateTimeConfig.getDefaultValue());

    private final List<CustomFilterHandler<Entity>> customFilterHandlers;
    private final List<CustomDateRangeHandler<Entity>> customDateRangeHandlers;

    @Override
    public Specification<Entity> buildSpecification(Map<String, String> filters, Map<String, String> dateRanges) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters != null) {
                filters.entrySet().stream()
                        .filter(e -> e.getValue() != null)
                        .forEach(e -> handleFilter(e, root, cb, predicates));
            }

            if (dateRanges != null) {
                dateRanges.entrySet().stream()
                        .filter(e -> e.getValue() != null)
                        .forEach(e -> handleDateRange(e, root, cb, predicates));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void handleFilter(Map.Entry<String, String> entry,
                              Root<Entity> root,
                              CriteriaBuilder cb,
                              List<Predicate> predicates) {

        String field = entry.getKey();
        String value = entry.getValue();
        log.debug("Processing filter: {}={}", field, value);

        customFilterHandlers.stream()
                .filter(h -> h.supports(field))
                .findFirst()
                .ifPresentOrElse(
                        h -> {
                            h.applyFilter(root, cb, predicates, value);
                            log.debug("Applied custom handler for '{}'", field);
                        },
                        () -> addGenericFilter(field, value, root, cb, predicates)
                );
    }

    private void addGenericFilter(String field, String value,
                                  Root<Entity> root, CriteriaBuilder cb,
                                  List<Predicate> predicates) {
        try {
            predicates.add(cb.equal(root.get(field), value));
            log.debug("Added generic filter: {}={}", field, value);
        } catch (IllegalArgumentException ex) {
            log.warn("Field '{}' not found on entity, skipped", field);
        }
    }

    private void handleDateRange(Map.Entry<String, String> entry,
                              Root<Entity> root,
                              CriteriaBuilder cb,
                              List<Predicate> predicates) {

        String field = entry.getKey();
        String value = entry.getValue();
        log.debug("Processing filter: {}={}", field, value);

        customDateRangeHandlers.stream()
                .filter(h -> h.supports(field))
                .findFirst()
                .ifPresentOrElse(
                        h -> {
                            h.applyDateRange(root, cb, predicates, value);
                            log.debug("Applied custom handler for '{}'", field);
                        },
                        () -> addGenericDateRange(field, value, root, cb, predicates)
                );
    }

    private void addGenericDateRange(String field, String value,
                                 Root<Entity> root,
                                 CriteriaBuilder cb,
                                 List<Predicate> predicates) {
        String[] dates = value.split(",");
        if (dates.length != 2) {
            log.warn("Invalid date range '{}'", value);
            return;
        }

        try {
            if (!"null".equals(dates[0])) {
                ZonedDateTime start = ZonedDateTime.parse(dates[0], DateRange.FORMATTER)
                        .withZoneSameInstant(USER_TIMEZONE);
                predicates.add(cb.greaterThanOrEqualTo(root.get(field),
                        start.toLocalDateTime()));
            }
            if (!"null".equals(dates[1])) {
                ZonedDateTime end = ZonedDateTime.parse(dates[1], DateRange.FORMATTER)
                        .withZoneSameInstant(USER_TIMEZONE);
                predicates.add(cb.lessThanOrEqualTo(root.get(field),
                        end.toLocalDateTime()));
            }
        } catch (DateTimeParseException ex) {
            log.error("Date parse error for '{}': {}", field, ex.getMessage());
        }
    }
}


