package centrikt.factory_monitoring.five_minute_report.dtos.responses;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ReportStatusResponse {
    private String controllerNumber;
    private String lineNumber;
    private ZonedDateTime lastReportTime;
    private String reportStatus;
}
