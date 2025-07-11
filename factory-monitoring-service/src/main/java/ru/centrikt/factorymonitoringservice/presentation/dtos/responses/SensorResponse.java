package ru.centrikt.factorymonitoringservice.presentation.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
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

    @Schema(description = "ИНН")
    private String taxpayerNumber;

    @Schema(description = "Номер комплекса и линии через нижнее подчеркивание")
    private String sensorNumber;
}
