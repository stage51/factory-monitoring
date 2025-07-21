package ru.centrikt.transportmonitoringservice.domain.exceptions;

public class FileDownloadException extends RuntimeException {
    public FileDownloadException(String message) {
        super(message);
    }
}
