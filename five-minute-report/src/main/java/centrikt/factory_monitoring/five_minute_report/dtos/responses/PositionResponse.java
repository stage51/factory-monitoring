package centrikt.factory_monitoring.five_minute_report.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@JacksonXmlRootElement(localName = "position")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Вывод позиции")
public class PositionResponse {

    private Long id;

    @Schema(description = "Вывод продукта")
    private ProductResponse product;

    @Schema(description = "ИНН")
    private String taxpayerNumber;

    @Schema(description = "Номер комплекса и линии через нижнее подчеркивание")
    private String sensorNumber;

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

    @Schema(description = "Статус: Неизвестно, Принято в РАР, Не принято в РАР, Принято в УТМ, Не принято в УТМ")
    private String status;

}
