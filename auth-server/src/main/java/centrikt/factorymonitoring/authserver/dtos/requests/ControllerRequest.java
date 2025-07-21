package centrikt.factorymonitoring.authserver.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Форма ввода устройства организации")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControllerRequest {
    private String serialNumber;
    private String govNumber;
    private String guid;
}
