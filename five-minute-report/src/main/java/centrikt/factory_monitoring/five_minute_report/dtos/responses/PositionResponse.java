package centrikt.factory_monitoring.five_minute_report.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@JacksonXmlRootElement(localName = "position")
@Builder
public class PositionResponse {

    private Long id;
    private ProductResponse product;
    private String taxpayerNumber;
    private ZonedDateTime controlDate;
    private BigDecimal vbsControl;
    @JsonProperty("aControl")
    private BigDecimal aControl;
    private BigDecimal percentAlc;
    private BigDecimal bottleCountControl;
    private BigDecimal temperature;
    private String mode;
    private String status;

}
