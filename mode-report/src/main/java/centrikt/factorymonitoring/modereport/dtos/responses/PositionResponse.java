package centrikt.factorymonitoring.modereport.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
public class PositionResponse {

    private Long id;
    private ProductResponse product;
    private String taxpayerNumber;
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
    private String status;
}
