package ru.centrikt.transportmonitoringservice.domain.exceptions;

public class FileUploadException extends RuntimeException {
    public FileUploadException(String message) {
        super(message);
    }
}
