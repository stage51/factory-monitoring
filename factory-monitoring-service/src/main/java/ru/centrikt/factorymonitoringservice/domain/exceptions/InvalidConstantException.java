package ru.centrikt.factorymonitoringservice.domain.exceptions;

public class InvalidConstantException extends RuntimeException {
    public InvalidConstantException(String message) {
        super(message);
    }
}
