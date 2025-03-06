package centrikt.factory_monitoring.five_minute_report.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@JacksonXmlRootElement(localName = "position")
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

    @Schema(description = "Дата")
    @NotNull(message = "Control date must not be null")
    private ZonedDateTime controlDate;

    @Schema(description = "Объем спирта")
    @NotNull(message = "VBS control value must not be null")
    @Digits(integer = 16, fraction = 2, message = "VBS control must be a valid decimal number with up to 16 digits and 2 decimal places")
    private BigDecimal vbsControl;

    @Schema(description = "Объем готовой продукции")
    @NotNull(message = "A control value must not be null")
    @Digits(integer = 16, fraction = 2, message = "A control must be a valid decimal number with up to 16 digits and 2 decimal places")
    @JsonProperty("aControl")
    private BigDecimal aControl;


    @Schema(description = "Концентрация спирта")
    @NotNull(message = "Percent alcohol must not be null")
    @Digits(integer = 13, fraction = 1, message = "Percent alcohol must be a valid decimal number with up to 13 digits and 1 decimal place")
    private BigDecimal percentAlc;

    @Schema(description = "Кол-во бутылок")
    @NotNull(message = "Bottle count control must not be null")
    @Digits(integer = 16, fraction = 0, message = "Bottle count control must be a valid integer number with up to 16 digits")
    private BigDecimal bottleCountControl;

    @Schema(description = "Температура")
    @NotNull(message = "Temperature must not be null")
    @Digits(integer = 3, fraction = 1, message = "Temperature must be a valid decimal number with up to 3 digits and 1 decimal place")
    private BigDecimal temperature;

    @Schema(description = "Режимы: кодовые обозначения")
    @NotNull(message = "Mode must not be null")
    private String mode;

    @Schema(description = "Статус: Неизвестно, Принято в РАР, Не принято в РАР, Принято в УТМ, Не принято в УТМ")
    @NotNull(message = "Status must not be null")
    private String status;

}
