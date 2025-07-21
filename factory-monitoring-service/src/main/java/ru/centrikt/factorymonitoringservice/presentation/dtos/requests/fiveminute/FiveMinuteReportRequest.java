package ru.centrikt.factorymonitoringservice.presentation.dtos.requests.fiveminute;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.SensorRequest;

@Data
@Schema(description = "Форма на создание, редактирование пятиминутного отчета")
public class FiveMinuteReportRequest {

    @Schema(description = "Форма информации о сенсоре")
    private SensorRequest sensor;

    @Schema(description = "Форма на создание, редактирование позиции")
    private FiveMinuteReportPositionRequest position;

    @Schema(description = "Статус: Неизвестно, Принято в РАР, Не принято в РАР, Принято в УТМ, Не принято в УТМ")
    @NotNull(message = "Status must not be null")
    private String status;
}
