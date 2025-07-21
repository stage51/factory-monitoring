package centrikt.factorymonitoring.authserver.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Schema(description = "Форма вывода устройства организации")
@AllArgsConstructor
@NoArgsConstructor
public class ControllerResponse {
    private Long id;
    private String serialNumber;
    private String govNumber;
    private String guid;
}


