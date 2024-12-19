package centrikt.factorymonitoring.authserver.exceptions.handlers;

import centrikt.factorymonitoring.authserver.exceptions.*;
import centrikt.factorymonitoring.authserver.exceptions.IllegalArgumentException;
import centrikt.factorymonitoring.authserver.utils.Message;
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
    @ExceptionHandler(InvalidConstraintException.class)
    public ResponseEntity<?> handleInvalidConstraintException(InvalidConstraintException ex){
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
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(new Message("error", HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }
    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<?> handleUserNotActiveException(UserNotActiveException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message("error", HttpStatus.FORBIDDEN.value(), ex.getMessage()));
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentialsException(InvalidCredentialsException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message("error", HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }
    @ExceptionHandler(ExpiredRecoveryException.class)
    public ResponseEntity<?> handleExpiredRecoveryException(ExpiredRecoveryException ex){
        return ResponseEntity.badRequest().body(new Message("error", HttpStatus.FORBIDDEN.value(), ex.getMessage()));
    }
}
