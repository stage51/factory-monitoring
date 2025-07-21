package ru.centrikt.transportmonitoringservice.domain.exceptions;

public class InvalidConstantException extends RuntimeException {
    public InvalidConstantException(String message) {
        super(message);
    }
}
