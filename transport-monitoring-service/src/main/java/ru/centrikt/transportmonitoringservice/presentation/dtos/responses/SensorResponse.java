package ru.centrikt.transportmonitoringservice.presentation.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Вывод информации о сенсоре")
public class SensorResponse {
    private Long id;

    @Schema(description = "Название организации")
    private String organizationName;

    @Schema(description = "Гос номер")
    private String govNumber;

    @Schema(description = "ИНН")
    private String taxpayerNumber;

    @Schema(description = "GUID")
    private String guid;
}
