package ru.centrikt.transportmonitoringservice.presentation.dtos.responses.navigation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.SensorResponse;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Вывод отчета по навигации")
public class NavigationReportResponse {
    private Long id;
    @Schema(description = "Вывод уровнемеров")
    private List<NavigationLevelGaugeResponse> levelGauges;
    @Schema(description = "Вывод сенсора")
    private SensorResponse sensor;
    @Schema(description = "Вывод данных по навигации")
    private NavigationDataResponse navigationData;
    @Schema(description = "Статус: Неизвестно, Принято в РАР, Не принято в РАР, Принято в УТМ, Не принято в УТМ")
    private String status;
    @Schema(description = "Дата создания")
    private ZonedDateTime createdAt;
    @Schema(description = "Дата изменения")
    private ZonedDateTime updatedAt;
    @Schema(description = "Название XML-файла на FTP-сервере")
    private String originalFilename;
}
