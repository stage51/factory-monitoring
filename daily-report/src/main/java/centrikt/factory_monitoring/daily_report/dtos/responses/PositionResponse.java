package centrikt.factory_monitoring.daily_report.dtos.responses;

import centrikt.factory_monitoring.daily_report.dtos.requests.ProductRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String sensorNumber;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private BigDecimal vbsStart;
    private BigDecimal vbsEnd;
    @JsonProperty("aStart")
    private BigDecimal aStart;
    @JsonProperty("aEnd")
    private BigDecimal aEnd;
    private BigDecimal percentAlc;
    private BigDecimal bottleCountStart;
    private BigDecimal bottleCountEnd;
    private BigDecimal temperature;
    private String mode;
    private BigDecimal crotonaldehyde;
    private BigDecimal toluene;
    private String status;
}
