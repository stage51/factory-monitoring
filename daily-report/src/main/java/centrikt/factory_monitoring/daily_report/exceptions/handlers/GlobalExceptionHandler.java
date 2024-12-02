package centrikt.factory_monitoring.daily_report.exceptions.handlers;

import centrikt.factory_monitoring.daily_report.Message;
import centrikt.factory_monitoring.daily_report.exceptions.EntityNotFoundException;
import centrikt.factory_monitoring.daily_report.exceptions.InvalidConstantException;
import centrikt.factory_monitoring.daily_report.exceptions.InvalidTokenException;
import centrikt.factory_monitoring.daily_report.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.badRequest().body(new Message("error", HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }
    @ExceptionHandler(InvalidConstantException.class)
    public ResponseEntity<?> handleInvalidConstantException(InvalidConstantException ex){
        return ResponseEntity.badRequest().body(new Message("error", HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException ex){
        return ResponseEntity.badRequest().body(new Message("error", HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException(InvalidTokenException ex){
        return ResponseEntity.badRequest().body(new Message("error", HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }
}
