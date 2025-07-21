package ru.centrikt.transportmonitoringservice.domain.enums;


import ru.centrikt.transportmonitoringservice.domain.exceptions.InvalidConstantException;

public enum ReportStatus {
    OK("OK"), WARN("WARN"), ERROR("ERROR");

    private final String reportStatus;

    ReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public String getReportStatus() { return reportStatus; }

    public static ReportStatus fromDescription(String description) {
        for (ReportStatus reportStatus : values()) {
            if (reportStatus.getReportStatus().equals(description)) {
                return reportStatus;
            }
        }
        throw new InvalidConstantException("Invalid ReportStatus description: " + description);
    }

    @Override
    public String toString() {
        return reportStatus;
    }
}
