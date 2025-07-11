package ru.centrikt.factorymonitoringservice.domain.exceptions;

public class MethodDisabledException extends RuntimeException {
    public MethodDisabledException(String message) {
        super(message);
    }
}
