package ru.centrikt.factorymonitoringservice.application.utils.ftp;

public enum FTPReportType {
    DAILY("daily"),
    FIVEMINUTE("five-minute"),
    MODE("mode");

    private final String value;

    FTPReportType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

