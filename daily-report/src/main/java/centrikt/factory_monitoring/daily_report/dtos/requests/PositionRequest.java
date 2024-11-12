package centrikt.factory_monitoring.daily_report.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@JacksonXmlRootElement(localName = "position")
public class PositionRequest {

    private ProductRequest product;

    @NotNull(message = "Start date must not be null")
    private ZonedDateTime startDate;

    @NotNull(message = "End date must not be null")
    private ZonedDateTime endDate;

    @NotNull(message = "VBS start must not be null")
    @Digits(integer = 16, fraction = 2, message = "VBS start must be a valid decimal number with up to 16 digits and 2 decimal places")
    private BigDecimal vbsStart;

    @NotNull(message = "VBS end must not be null")
    @Digits(integer = 16, fraction = 2, message = "VBS end must be a valid decimal number with up to 16 digits and 2 decimal places")
    private BigDecimal vbsEnd;

    @NotNull(message = "A start must not be null")
    @Digits(integer = 16, fraction = 2, message = "A start must be a valid decimal number with up to 16 digits and 2 decimal places")
    @JsonProperty("aStart")
    private BigDecimal aStart;

    @NotNull(message = "A end must not be null")
    @Digits(integer = 16, fraction = 2, message = "A end must be a valid decimal number with up to 16 digits and 2 decimal places")
    @JsonProperty("aEnd")
    private BigDecimal aEnd;

    @NotNull(message = "Percent alcohol must not be null")
    @Digits(integer = 13, fraction = 1, message = "Percent alcohol must be a valid decimal number with up to 13 digits and 1 decimal place")
    private BigDecimal percentAlc;

    @NotNull(message = "Bottle count start must not be null")
    @Digits(integer = 16, fraction = 0, message = "Bottle count start must be a valid integer number with up to 16 digits")
    private BigDecimal bottleCountStart;

    @NotNull(message = "Bottle count end must not be null")
    @Digits(integer = 16, fraction = 0, message = "Bottle count end must be a valid integer number with up to 16 digits")
    private BigDecimal bottleCountEnd;

    @NotNull(message = "Temperature must not be null")
    @Digits(integer = 3, fraction = 1, message = "Temperature must be a valid decimal number with up to 3 digits and 1 decimal place")
    private BigDecimal temperature;

    @NotNull(message = "Mode must not be null")
    private String mode;

    @Digits(integer = 1, fraction = 2, message = "Crotonaldehyde must be a valid decimal number with up to 1 digit and 2 decimal places")
    private BigDecimal crotonaldehyde;

    @Digits(integer = 1, fraction = 2, message = "Toluene must be a valid decimal number with up to 1 digit and 2 decimal places")
    private BigDecimal toluene;

    @NotNull(message = "Status must not be null")
    private String status;
}
