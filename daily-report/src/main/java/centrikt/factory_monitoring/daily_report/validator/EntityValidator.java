package centrikt.factory_monitoring.daily_report.validator;

public interface EntityValidator {
    <T> void validate(T dto);
}

