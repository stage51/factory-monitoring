package centrikt.factory_monitoring.daily_report.dtos.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class ReportMessage {
    private String taxpayerNumber;
    private String sensorNumber;
    private String reportType;
    private String message;
}