package centrikt.factory_monitoring.five_minute_report.utils.validator;

public interface EntityValidator {
    <T> void validate(T dto);
}
