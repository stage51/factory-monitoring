package centrikt.factory_monitoring.five_minute_report.dtos.responses;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JacksonXmlRootElement(localName = "product")
@Builder
public class ProductResponse {

    private Long id;
    private String unitType;
    private String type;
    private String fullName;
    private String shortName;
    private String alcCode;
    private BigDecimal capacity;
    private BigDecimal alcVolume;
    private String productVCode;
    private BigDecimal crotonaldehyde;
    private BigDecimal toluene;
}

