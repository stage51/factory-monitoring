package centrikt.factorymonitoring.authserver.utils.entityvalidator;

public interface EntityValidator {
    <T> void validate(T dto);
}
