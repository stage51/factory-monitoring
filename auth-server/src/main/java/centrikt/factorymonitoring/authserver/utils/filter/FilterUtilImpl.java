package centrikt.factorymonitoring.authserver.utils.filter;

import centrikt.factorymonitoring.authserver.configs.DateTimeConfig;
import centrikt.factorymonitoring.authserver.dtos.extra.DateRange;
import centrikt.factorymonitoring.authserver.models.enums.Role;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
@RefreshScope
public class FilterUtilImpl<Entity> implements FilterUtil<Entity> {

    private static final ZoneId USER_TIMEZONE = ZoneId.of(DateTimeConfig.getDefaultValue());

    public Specification<Entity> buildSpecification(Map<String, String> filters, Map<String, String> dateRanges) {
        log.trace("Entering buildSpecification with filters: {} and dateRanges: {}", filters, dateRanges);

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters != null) {
                filters.entrySet().stream()
                        .filter(entry -> entry.getValue() != null)
                        .forEach(entry -> {
                            String field = entry.getKey();
                            String value = entry.getValue();
                            log.debug("Processing filter: field={}, value={}", field, value);

                            try {
                                if ("organization".equals(field)) {
                                    predicates.add(criteriaBuilder.like(
                                            criteriaBuilder.lower(root.get("organization").get("shortName")),
                                            "%" + value.toLowerCase() + "%"));
                                    log.debug("Added filter for organization with value: {}", value);
                                } else if ("role".equals(field)) {
                                    Role role = Role.valueOf(value);
                                    predicates.add(criteriaBuilder.equal(root.get(field), role));
                                    log.debug("Added filter for role: {}", role);
                                } else if ("active".equals(field)) {
                                    predicates.add(criteriaBuilder.equal(root.get("active"), Boolean.parseBoolean(value)));
                                    log.debug("Added filter for active status with value: {}", value);
                                } else {
                                    predicates.add(criteriaBuilder.equal(root.get(field), value));
                                    log.debug("Added generic filter for field: {} with value: {}", field, value);
                                }
                            } catch (Exception e) {
                                log.error("Error processing filter for field: {} with value: {}", field, value, e);
                            }
                        });
            }

            if (dateRanges != null) {
                dateRanges.entrySet().stream()
                        .filter(entry -> entry.getValue() != null)
                        .forEach(entry -> {
                            log.debug("Processing date range filter for: {} => {}", entry.getKey(), entry.getValue());
                            String[] dates = entry.getValue().split(",");
                            if (dates.length == 2) {
                                try {
                                    if (!"null".equals(dates[0])) {
                                        ZonedDateTime startDate = ZonedDateTime.parse(dates[0], DateRange.FORMATTER)
                                                .withZoneSameInstant(USER_TIMEZONE);
                                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                                                root.get(entry.getKey()), startDate.toLocalDateTime()));
                                        log.debug("Added start date filter for {}: {}", entry.getKey(), startDate);
                                    }

                                    if (!"null".equals(dates[1])) {
                                        ZonedDateTime endDate = ZonedDateTime.parse(dates[1], DateRange.FORMATTER)
                                                .withZoneSameInstant(USER_TIMEZONE);
                                        predicates.add(criteriaBuilder.lessThanOrEqualTo(
                                                root.get(entry.getKey()), endDate.toLocalDateTime()));
                                        log.debug("Added end date filter for {}: {}", entry.getKey(), endDate);
                                    }
                                } catch (DateTimeParseException e) {
                                    log.error("Date parsing error for field {}: {}", entry.getKey(), e.getMessage());
                                }
                            } else {
                                log.warn("Invalid date range format for field {}: {}", entry.getKey(), entry.getValue());
                            }
                        });
            }

            log.trace("Exiting buildSpecification with predicates: {}", predicates);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}


