package centrikt.factory_monitoring.daily_report.utils.filter;

import centrikt.factory_monitoring.daily_report.configs.DateTimeConfig;
import centrikt.factory_monitoring.daily_report.dtos.extra.DateRange;
import centrikt.factory_monitoring.daily_report.enums.Mode;
import centrikt.factory_monitoring.daily_report.enums.Status;
import centrikt.factory_monitoring.daily_report.enums.Type;
import centrikt.factory_monitoring.daily_report.enums.UnitType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
                                if ("product".equals(field)) {
                                    predicates.add(criteriaBuilder.like(
                                            criteriaBuilder.lower(root.get("product").get("productVCode")),
                                            "%" + value.toLowerCase() + "%"));
                                    log.debug("Added filter for product with value: {}", value);
                                } else if ("sensorNumber".equals(field)) {
                                    if (value.matches("\\d+_\\d+")) {
                                        String controllerNumber = value.split("_")[0];
                                        String lineNumber = value.split("_")[1];
                                        predicates.add(criteriaBuilder.equal(root.get("controllerNumber"), controllerNumber));
                                        predicates.add(criteriaBuilder.equal(root.get("lineNumber"), lineNumber));
                                        log.debug("Added filter for sensorNumber with controllerNumber: {} and lineNumber: {}", controllerNumber, lineNumber);
                                    } else {
                                        log.warn("Invalid sensorNumber format: {}", value);
                                    }
                                } else if ("status".equals(field)) {
                                    Status status = Status.fromDescription(value);
                                    predicates.add(criteriaBuilder.equal(root.get(field), status));
                                    log.debug("Added filter for status: {}", status);
                                } else if ("mode".equals(field)) {
                                    Mode mode = Mode.fromCode(value);
                                    predicates.add(criteriaBuilder.equal(root.get(field), mode));
                                    log.debug("Added filter for mode: {}", mode);
                                } else if ("type".equals(field)) {
                                    Type type = Type.fromString(value);
                                    predicates.add(criteriaBuilder.equal(root.get(field), type));
                                    log.debug("Added filter for type: {}", type);
                                } else if ("unitType".equals(field)) {
                                    UnitType unitType = UnitType.fromString(value);
                                    predicates.add(criteriaBuilder.equal(root.get(field), unitType));
                                    log.debug("Added filter for unitType: {}", unitType);
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


