package centrikt.factory_monitoring.daily_report.utils.filter;

import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public interface FilterUtil<Entity> {
    Specification<Entity> buildSpecification(Map<String, String> filters, Map<String, String> dateRanges);
}
