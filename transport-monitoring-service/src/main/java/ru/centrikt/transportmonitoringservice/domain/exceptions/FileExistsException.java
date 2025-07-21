package ru.centrikt.transportmonitoringservice.domain.exceptions;

public class FileExistsException extends RuntimeException {
    public FileExistsException(String message) {
        super(message);
    }
}
