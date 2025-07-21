package ru.centrikt.transportmonitoringservice.presentation.dtos.requests.mode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.SensorRequest;


@Data
@Schema(description = "Форма на создание, редактирование сессии")
public class ModeReportRequest {

    @Schema(description = "Форма информации о сенсоре")
    private SensorRequest sensor;

    @Schema(description = "Форма на создание, редактирование позиции")
    private ModePositionRequest position;
}
