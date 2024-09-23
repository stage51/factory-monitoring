package centrikt.factory_monitoring.daily_report;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private String type;
    private Integer code;
    private String message;
}
