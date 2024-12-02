package centrikt.factory_monitoring.five_minute_report.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@JacksonXmlRootElement(localName = "position")
public class PositionRequest {

    private ProductRequest product;

    @NotNull(message = "Taxpayer number must not be null")
    @Length(min = 12, max = 12, message = "Taxpayer number consist of 12 characters")
    private String taxpayerNumber;

    @NotNull(message = "Control date must not be null")
    private ZonedDateTime controlDate;

    @NotNull(message = "VBS control value must not be null")
    @Digits(integer = 16, fraction = 2, message = "VBS control must be a valid decimal number with up to 16 digits and 2 decimal places")
    private BigDecimal vbsControl;

    @NotNull(message = "A control value must not be null")
    @Digits(integer = 16, fraction = 2, message = "A control must be a valid decimal number with up to 16 digits and 2 decimal places")
    @JsonProperty("aControl")
    private BigDecimal aControl;

    @NotNull(message = "Percent alcohol must not be null")
    @Digits(integer = 13, fraction = 1, message = "Percent alcohol must be a valid decimal number with up to 13 digits and 1 decimal place")
    private BigDecimal percentAlc;

    @NotNull(message = "Bottle count control must not be null")
    @Digits(integer = 16, fraction = 0, message = "Bottle count control must be a valid integer number with up to 16 digits")
    private BigDecimal bottleCountControl;

    @NotNull(message = "Temperature must not be null")
    @Digits(integer = 3, fraction = 1, message = "Temperature must be a valid decimal number with up to 3 digits and 1 decimal place")
    private BigDecimal temperature;

    @NotNull(message = "Mode must not be null")
    private String mode;

    @NotNull(message = "Status must not be null")
    private String status;

}
