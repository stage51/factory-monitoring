package ru.centrikt.transportmonitoringservice.presentation.dtos.responses;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.ZonedDateTime;

@Schema(description = "Статус линий")
@Data
public class ReportStatusResponse {
    @Schema(description = "Номер контроллера/комлпекса")
    private String controllerNumber;

    @Schema(description = "Номер линии")
    private String lineNumber;

    @Schema(description = "Дата последнего отчета")
    private ZonedDateTime lastReportTime;

    @Schema(description = "Статус: OK, WARN, ERROR")
    private String reportStatus;
}
