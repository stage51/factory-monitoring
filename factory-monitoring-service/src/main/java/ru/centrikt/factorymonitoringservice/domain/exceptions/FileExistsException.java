package ru.centrikt.factorymonitoringservice.domain.exceptions;

public class FileExistsException extends RuntimeException {
    public FileExistsException(String message) {
        super(message);
    }
}
