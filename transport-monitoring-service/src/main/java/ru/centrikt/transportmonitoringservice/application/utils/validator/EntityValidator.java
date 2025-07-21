package ru.centrikt.transportmonitoringservice.application.utils.validator;

public interface EntityValidator {
    <T> void validate(T dto);
}

