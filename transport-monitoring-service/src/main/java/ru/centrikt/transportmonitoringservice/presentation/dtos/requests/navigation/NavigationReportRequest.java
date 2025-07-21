package ru.centrikt.transportmonitoringservice.presentation.dtos.requests.navigation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.SensorRequest;

import java.util.List;

@Data
@Schema(description = "Форма на создание, редактирование отчета по навигации")
public class NavigationReportRequest {
    @Schema(description = "Форма на создание, редактирование уровнемеров")
    private List<NavigationLevelGaugeRequest> levelGauges;
    @Schema(description = "Форма на создание, редактирование сенсора")
    private SensorRequest sensor;
    @Schema(description = "Форма на создание, редактирование данных по навигации")
    private NavigationDataRequest navigationData;
    @Schema(description = "Статус: Неизвестно, Принято в РАР, Не принято в РАР, Принято в УТМ, Не принято в УТМ")
    @NotNull(message = "Status must not be null")
    private String status;
}
