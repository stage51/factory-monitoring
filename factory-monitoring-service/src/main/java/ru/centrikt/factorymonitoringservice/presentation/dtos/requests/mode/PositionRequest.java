package ru.centrikt.factorymonitoringservice.presentation.dtos.requests.mode;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Schema(description = "Форма на создание, редактирование позиции")
public class PositionRequest {

    @Schema(description = "Форма на создание, редактирование продукта")
    private ProductRequest product;

    @Schema(description = "ИНН")
    @NotNull(message = "Taxpayer number must not be null")
    @Length(min = 12, max = 12, message = "Taxpayer number consist of 12 characters")
    private String taxpayerNumber;

    @Schema(description = "Номер комплекса и линии через нижнее подчеркивание")
    @NotNull
    @Pattern(regexp = "\\d+_\\d+", message = "Format must be number_number, for example 12_34")
    private String sensorNumber;

    @Schema(description = "Дата начала измерений")
    @NotNull(message = "Start date must not be null")
    private ZonedDateTime startDate;

    @Schema(description = "Дата конца измерений")
    @NotNull(message = "End date must not be null")
    private ZonedDateTime endDate;

    @Schema(description = "Объем безводного спирта в начале")
    @NotNull(message = "VBS start must not be null")
    @Digits(integer = 16, fraction = 2, message = "VBS start must be a valid decimal number with up to 16 digits and 2 decimal places")
    private BigDecimal vbsStart;

    @Schema(description = "Объем безводного спирта в конце")
    @NotNull(message = "VBS end must not be null")
    @Digits(integer = 16, fraction = 2, message = "VBS end must be a valid decimal number with up to 16 digits and 2 decimal places")
    private BigDecimal vbsEnd;

    @Schema(description = "Объем готовой продукции в начале")
    @NotNull(message = "A start must not be null")
    @Digits(integer = 16, fraction = 2, message = "A start must be a valid decimal number with up to 16 digits and 2 decimal places")
    @JsonProperty("aStart")
    private BigDecimal aStart;

    @Schema(description = "Объем готовой продукции в конце")
    @NotNull(message = "A end must not be null")
    @Digits(integer = 16, fraction = 2, message = "A end must be a valid decimal number with up to 16 digits and 2 decimal places")
    @JsonProperty("aEnd")
    private BigDecimal aEnd;

    @Schema(description = "Концентрация спирта")
    @NotNull(message = "Percent alcohol must not be null")
    @Digits(integer = 13, fraction = 1, message = "Percent alcohol must be a valid decimal number with up to 13 digits and 1 decimal place")
    private BigDecimal percentAlc;

    @Schema(description = "Кол-во бутылок в начале")
    @NotNull(message = "Bottle count start must not be null")
    @Digits(integer = 16, fraction = 0, message = "Bottle count start must be a valid integer number with up to 16 digits")
    private BigDecimal bottleCountStart;

    @Schema(description = "Кол-во бутылок в конце")
    @Digits(integer = 16, fraction = 0, message = "Bottle count end must be a valid integer number with up to 16 digits")
    private BigDecimal bottleCountEnd;

    @Schema(description = "Температура")
    @NotNull(message = "Temperature must not be null")
    @Digits(integer = 3, fraction = 1, message = "Temperature must be a valid decimal number with up to 3 digits and 1 decimal place")
    private BigDecimal temperature;

    @Schema(description = "Режимы: кодовые обозначения")
    @NotNull(message = "Mode must not be null")
    private String mode;
}