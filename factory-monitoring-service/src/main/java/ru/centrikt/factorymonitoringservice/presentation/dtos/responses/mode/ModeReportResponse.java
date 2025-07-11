package ru.centrikt.factorymonitoringservice.presentation.dtos.responses.mode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.SensorResponse;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Вывод сессии")
public class ModeReportResponse {
    private Long id;

    @Schema(description = "Вывод информации о сенсоре")
    private SensorResponse sensor;

    @Schema(description = "Вывод позиции")
    private ModePositionResponse position;

    @Schema(description = "Дата создания")
    private ZonedDateTime createdAt;

    @Schema(description = "Дата изменения")
    private ZonedDateTime updatedAt;
    @Schema(description = "Название XML-файла на FTP-сервере")
    private String originalFilename;
}
