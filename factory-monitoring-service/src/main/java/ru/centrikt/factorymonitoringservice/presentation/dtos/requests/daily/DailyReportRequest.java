package ru.centrikt.factorymonitoringservice.presentation.dtos.requests.daily;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.SensorRequest;

import java.util.List;

@Data
@Schema(description = "Форма на создание, редактирование дневного отчета")
public class DailyReportRequest {

    @Schema(description = "Форма информации о сенсоре")
    private SensorRequest sensor;

    @Schema(description = "Форма на создание, редактирование позиции")
    private List<DailyReportPositionRequest> positions;

    @Schema(description = "Статус: Неизвестно, Принято в РАР, Не принято в РАР, Принято в УТМ, Не принято в УТМ")
    @NotNull(message = "Status must not be null")
    private String status;
}
