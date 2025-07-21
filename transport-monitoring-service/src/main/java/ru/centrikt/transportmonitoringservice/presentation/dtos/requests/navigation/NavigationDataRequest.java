package ru.centrikt.transportmonitoringservice.presentation.dtos.requests.navigation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Schema(description = "Форма на создание, редактирование данных по навигации")
public class NavigationDataRequest {
    @Schema(description = "Дата и время навигации")
    @NotNull(message = "Navigation date must not be null")
    private ZonedDateTime navigationDate;
    @Schema(description = "Широта")
    @NotNull(message = "Latitude must not be null")
    @Digits(integer = 2, fraction = 15, message = "Readings must be a valid decimal number with up to 2 digits and 15 decimal places")
    private BigDecimal latitude;
    @Schema(description = "Долгота")
    @NotNull(message = "Longitude must not be null")
    @Digits(integer = 2, fraction = 15, message = "Readings must be a valid decimal number with up to 2 digits and 15 decimal places")
    private BigDecimal longitude;
    @Schema(description = "Количество спутников")
    @NotNull(message = "Count Satellite must not be null")
    private Long countSatellite;
    @Schema(description = "Количество спутников")
    @NotNull(message = "Accuracy must not be null")
    private Long accuracy;
    @Schema(description = "Курс")
    @NotNull(message = "Course must not be null")
    private Long course;
    @Schema(description = "Скорость")
    @NotNull(message = "Speed must not be null")
    private Long speed;
}
