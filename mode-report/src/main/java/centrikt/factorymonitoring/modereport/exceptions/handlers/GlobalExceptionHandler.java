package centrikt.factorymonitoring.modereport.exceptions.handlers;

import centrikt.factorymonitoring.modereport.exceptions.*;
import centrikt.factorymonitoring.modereport.utils.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("error", HttpStatus.NOT_FOUND.value(), ex.getMessage()));
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
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message("error", HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }
    @ExceptionHandler(MethodDisabledException.class)
    public ResponseEntity<?> handleMethodDisabledException(MethodDisabledException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message("error", HttpStatus.FORBIDDEN.value(), ex.getMessage()));
    }
}
