package ru.centrikt.factorymonitoringservice.application.utils.validator;

public interface EntityValidator {
    <T> void validate(T dto);
}

