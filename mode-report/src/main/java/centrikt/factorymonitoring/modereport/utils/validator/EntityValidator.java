package centrikt.factorymonitoring.modereport.utils.validator;

public interface EntityValidator {
    <T> void validate(T dto);
}

