package ru.centrikt.transportmonitoringservice.domain.exceptions.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.centrikt.transportmonitoringservice.application.utils.Message;
import ru.centrikt.transportmonitoringservice.domain.exceptions.*;

import java.io.IOException;

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
    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException ex){
        return ResponseEntity.internalServerError().body(new Message("error", HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }
    @ExceptionHandler(MethodDisabledException.class)
    public ResponseEntity<?> handleMethodDisabledException(MethodDisabledException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message("error", HttpStatus.FORBIDDEN.value(), ex.getMessage()));
    }
    @ExceptionHandler(EntityMappingException.class)
    public ResponseEntity<?> handleEntityMappingException(EntityMappingException ex){
        return ResponseEntity.internalServerError().body(new Message("error", HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }
    @ExceptionHandler(FileExistsException.class)
    public ResponseEntity<?> handleFileExistsException(FileExistsException ex){
        return ResponseEntity.badRequest().body(new Message("error", HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }
    @ExceptionHandler(FileDownloadException.class)
    public ResponseEntity<?> handleFileDownloadException(FileDownloadException ex){
        return ResponseEntity.internalServerError().body(new Message("error", HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<?> handleFileUploadException(FileUploadException ex){
        return ResponseEntity.internalServerError().body(new Message("error", HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }
}
