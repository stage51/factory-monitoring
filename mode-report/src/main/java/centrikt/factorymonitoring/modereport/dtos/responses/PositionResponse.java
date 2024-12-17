package centrikt.factorymonitoring.modereport.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@JacksonXmlRootElement(localName = "position")
@Builder
@Schema(description = "Вывод позиции")
public class PositionResponse {

    private Long id;
    @Schema(description = "Вывод продукта")
    private ProductResponse product;

    @Schema(description = "ИНН")
    private String taxpayerNumber;

    @Schema(description = "Номер комплекса и линии через нижнее подчеркивание")
    private String sensorNumber;

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

    @Schema(description = "Режим: Промывка АСИиУ, Калибровка АСИиУ, Технологический прогон, Производство продукции, Остановка АСИиУ, " +
            "Прием (возврат), Прием (закупка), Внутреннее перемещение, Отгрузка (покупателю), Отгрузка (возврат)")
    private String mode;

    @Schema(description = "Статус: Неизвестно, Принято в РАР, Не принято в РАР, Принято в УТМ, Не принято в УТМ")
    private String status;
}

