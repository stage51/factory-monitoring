package centrikt.factory_monitoring.daily_report.validator;

import centrikt.factory_monitoring.daily_report.exceptions.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EntityValidatorImpl implements EntityValidator {

    private Validator validator;

    public EntityValidatorImpl(Validator validator) {
        this.validator = validator;
    }

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("\n");
            }
            throw new ValidationException("Validation failed: \n" + sb.toString());
        }
    }
}

