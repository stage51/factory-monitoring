package ru.centrikt.transportmonitoringservice.presentation.dtos.responses.daily;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Вывод позиции")
public class DailyPositionResponse {

    private Long id;

    @Schema(description = "Вывод продукта")
    private DailyProductResponse product;

    @Schema(description = "Дата начала измерений")
    private ZonedDateTime startDate;

    @Schema(description = "Дата конца измерений")
    private ZonedDateTime endDate;

    @Schema(description = "Объем безводного спирта в начале")
    private BigDecimal vbsStart;

    @Schema(description = "Объем безводного спирта в конце")
    private BigDecimal vbsEnd;

    @Schema(description = "Объем готовой продукции в начале")
    @JsonProperty("aStart")
    private BigDecimal aStart;

    @Schema(description = "Объем готовой продукции в конце")
    @JsonProperty("aEnd")
    private BigDecimal aEnd;

    @Schema(description = "Концентрация спирта")
    private BigDecimal percentAlc;

    @Schema(description = "Кол-во бутылок в начале")
    private BigDecimal bottleCountStart;

    @Schema(description = "Кол-во бутылок в конце")
    private BigDecimal bottleCountEnd;

    @Schema(description = "Температура")
    private BigDecimal temperature;

    @Schema(description = "Режимы: наименования кодов")
    private String mode;
}
