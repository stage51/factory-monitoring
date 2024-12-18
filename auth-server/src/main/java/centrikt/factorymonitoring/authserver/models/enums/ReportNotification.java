package centrikt.factorymonitoring.authserver.models.enums;

public enum ReportNotification {
    DAILY(0), FIVE_MINUTE(1), MODE(2);

    private final int reportNotification;

    ReportNotification(int reportNotification) {
        this.reportNotification = reportNotification;
    }

    public int getReportNotification() { return reportNotification; }

    public String toString() { return name(); }
}
