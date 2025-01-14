package centrikt.factorymonitoring.authserver.utils.entityvalidator;

import centrikt.factorymonitoring.authserver.exceptions.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class EntityValidatorImpl implements EntityValidator {

    private Validator validator;

    public EntityValidatorImpl(Validator validator) {
        this.validator = validator;
        log.info("EntityValidatorImpl initialized with validator: {}", validator.getClass().getName());
    }

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
        log.debug("Validator set to: {}", validator.getClass().getName());
    }

    public <T> void validate(T dto) {
        log.debug("Starting validation for: {}", dto.getClass().getName());

        Set<ConstraintViolation<T>> violations = validator.validate(dto);

        if (violations.isEmpty()) {
            log.trace("No violations found for: {}", dto.getClass().getName());
        } else {
            StringBuilder sb = new StringBuilder();
            log.warn("Validation failed for: {}", dto.getClass().getName());

            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append(" ");
                log.error("Violation - {}: {}", violation.getPropertyPath(), violation.getMessage());
            }
            throw new ValidationException("Validation failed: " + sb.toString());
        }
    }
}

