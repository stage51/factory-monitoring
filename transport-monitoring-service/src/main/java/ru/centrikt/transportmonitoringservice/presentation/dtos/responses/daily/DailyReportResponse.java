package ru.centrikt.transportmonitoringservice.presentation.dtos.responses.daily;

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
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Вывод дневного отчета")
public class DailyReportResponse {
    private Long id;

    @Schema(description = "Вывод информации о сенсоре")
    private SensorResponse sensor;

    @Schema(description = "Вывод позиции")
    private List<DailyPositionResponse> positions;

    @Schema(description = "Дата создания")
    private ZonedDateTime createdAt;

    @Schema(description = "Дата изменения")
    private ZonedDateTime updatedAt;

    @Schema(description = "Статус: Неизвестно, Принято в РАР, Не принято в РАР, Принято в УТМ, Не принято в УТМ")
    private String status;
    @Schema(description = "Название XML-файла на FTP-сервере")
    private String originalFilename;
}
