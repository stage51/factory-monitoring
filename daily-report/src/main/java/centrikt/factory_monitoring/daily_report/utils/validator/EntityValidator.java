package centrikt.factory_monitoring.daily_report.utils.validator;

public interface EntityValidator {
    <T> void validate(T dto);
}

