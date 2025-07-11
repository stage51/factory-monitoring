package ru.centrikt.factorymonitoringservice.presentation.dtos.requests.mode;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.SensorRequest;

@Data
@Schema(description = "Форма на создание, редактирование сессии")
public class ModeReportRequest {

    @Schema(description = "Вывод информации о сенсоре")
    private SensorRequest sensor;

    @Schema(description = "Форма на создание, редактирование позиции")
    private PositionRequest position;
}
