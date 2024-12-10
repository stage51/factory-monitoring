package centrikt.factory_monitoring.five_minute_report.utils.filter;

import centrikt.factory_monitoring.five_minute_report.configs.DateTimeConfig;
import centrikt.factory_monitoring.five_minute_report.dtos.extra.DateRange;

import centrikt.factory_monitoring.five_minute_report.enums.Mode;
import centrikt.factory_monitoring.five_minute_report.enums.Status;
import centrikt.factory_monitoring.five_minute_report.enums.Type;
import centrikt.factory_monitoring.five_minute_report.enums.UnitType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;


@Component
@Slf4j
public class FilterUtilImpl<Entity> implements FilterUtil<Entity> {
    private static final ZoneId USER_TIMEZONE = ZoneId.of(DateTimeConfig.getDefaultValue());

    public Specification<Entity> buildSpecification(Map<String, String> filters, Map<String, String> dateRanges) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters != null) {
                filters.entrySet().stream()
                        .filter(entry -> entry.getValue() != null)
                        .forEach(entry -> {
                            String field = entry.getKey();
                            String value = entry.getValue();
                            if ("product".equals(field)) {
                                predicates.add(criteriaBuilder.like(
                                        criteriaBuilder.lower(root.get("product").get("productVCode")),
                                        "%" + value.toLowerCase() + "%"));
                            } else if ("sensorNumber".equals(field)) {
                                if (field.matches("\\d+_\\d+")) {
                                    String controllerNumber = value.split("_")[0];
                                    String lineNumber = value.split("_")[1];
                                    predicates.add(criteriaBuilder.equal(root.get("controllerNumber"), controllerNumber));
                                    predicates.add(criteriaBuilder.equal(root.get("lineNumber"), lineNumber.startsWith("0") ? lineNumber.substring(1) : lineNumber));
                                }
                            } else if ("status".equals(field)) {
                                Status status = Status.fromDescription(value);
                                predicates.add(criteriaBuilder.equal(root.get(field), status));
                            } else if ("mode".equals(field)) {
                                Mode mode = Mode.fromDescription(value);
                                predicates.add(criteriaBuilder.equal(root.get(field), mode));
                            } else if ("type".equals(field)) {
                                Type type = Type.fromString(value);
                                predicates.add(criteriaBuilder.equal(root.get(field), type));
                            } else if ("unitType".equals(field)) {
                                UnitType unitType = UnitType.fromString(value);
                                predicates.add(criteriaBuilder.equal(root.get(field), unitType));
                            } else {
                                predicates.add(criteriaBuilder.equal(root.get(field), value));
                            }
                        });
            }

            if (dateRanges != null) {
                dateRanges.entrySet().stream()
                        .filter(entry -> entry.getValue() != null)
                        .forEach(entry -> {
                            log.info("Date range filter for: " + entry.getKey() + " => " + entry.getValue());
                            String[] dates = entry.getValue().split(",");
                            if (dates.length == 2) {
                                try {
                                    if (!"null".equals(dates[0])) {
                                        ZonedDateTime startDate = ZonedDateTime.parse(dates[0], DateRange.FORMATTER)
                                                .withZoneSameInstant(USER_TIMEZONE);
                                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                                                root.get(entry.getKey()), startDate.toLocalDateTime()));
                                    }

                                    if (!"null".equals(dates[1])) {
                                        ZonedDateTime endDate = ZonedDateTime.parse(dates[1], DateRange.FORMATTER)
                                                .withZoneSameInstant(USER_TIMEZONE);
                                        predicates.add(criteriaBuilder.lessThanOrEqualTo(
                                                root.get(entry.getKey()), endDate.toLocalDateTime()));
                                    }
                                } catch (DateTimeParseException e) {
                                    log.error("Date parsing error for field " + entry.getKey() + ": " + e.getMessage());
                                }
                            }
                        });
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

