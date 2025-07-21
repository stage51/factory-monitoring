package ru.centrikt.transportmonitoringservice.presentation.dtos.responses.navigation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Вывод уровнемеров")
public class NavigationLevelGaugeResponse {
    private Long id;
    @Schema(description = "Номер")
    private Long number;
    @Schema(description = "Уровень")
    private BigDecimal readings;
    @Schema(description = "Температура")
    private BigDecimal temperature;
    @Schema(description = "Плотность")
    private BigDecimal density;
}
