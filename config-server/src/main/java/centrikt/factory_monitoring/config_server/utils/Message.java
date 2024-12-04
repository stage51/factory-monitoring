package centrikt.factory_monitoring.config_server.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private String type;
    private Integer code;
    private String message;
}
