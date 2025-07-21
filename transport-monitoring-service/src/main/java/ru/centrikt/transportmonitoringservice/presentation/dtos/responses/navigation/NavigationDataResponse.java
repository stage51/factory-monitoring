package ru.centrikt.transportmonitoringservice.presentation.dtos.responses.navigation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Вывод данных по навигации")
public class NavigationDataResponse {
    private Long id;
    @Schema(description = "Дата и время навигации")
    private ZonedDateTime navigationDate;
    @Schema(description = "Широта")
    private BigDecimal latitude;
    @Schema(description = "Долгота")
    private BigDecimal longitude;
    @Schema(description = "Количество спутников")
    private Long countSatellite;
    @Schema(description = "Количество спутников")
    private Long accuracy;
    @Schema(description = "Курс")
    private Long course;
    @Schema(description = "Скорость")
    private Long speed;
}
