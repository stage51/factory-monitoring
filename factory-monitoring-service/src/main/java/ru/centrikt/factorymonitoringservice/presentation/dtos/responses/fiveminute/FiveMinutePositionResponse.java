package ru.centrikt.factorymonitoringservice.presentation.dtos.responses.fiveminute;

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
public class FiveMinutePositionResponse {

    private Long id;

    @Schema(description = "Вывод продукта")
    private FiveMinuteProductResponse product;

    @Schema(description = "Дата")
    private ZonedDateTime controlDate;

    @Schema(description = "Объем спирта")
    private BigDecimal vbsControl;
    @JsonProperty("aControl")

    @Schema(description = "Объем готовой продукции")
    private BigDecimal aControl;

    @Schema(description = "Концентрация спирта")
    private BigDecimal percentAlc;

    @Schema(description = "Кол-во бутылок")
    private BigDecimal bottleCountControl;

    @Schema(description = "Температура")
    private BigDecimal temperature;

    @Schema(description = "Режимы: наименования кодов")
    private String mode;

}
