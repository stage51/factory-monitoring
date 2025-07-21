package ru.centrikt.transportmonitoringservice.domain.exceptions;

public class MethodDisabledException extends RuntimeException {
    public MethodDisabledException(String message) {
        super(message);
    }
}
