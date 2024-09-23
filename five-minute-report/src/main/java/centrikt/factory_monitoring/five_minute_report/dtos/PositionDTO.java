package centrikt.factory_monitoring.five_minute_report.dtos;

import centrikt.factory_monitoring.five_minute_report.enums.Mode;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JacksonXmlRootElement(localName = "position")
public class PositionDTO {

    @NotNull(message = "Product must not be null")
    private ProductDTO product;

    @NotNull(message = "Control date must not be null")
    private LocalDateTime controlDate;

    @NotNull(message = "VBS control value must not be null")
    @Digits(integer = 16, fraction = 2, message = "VBS control must be a valid decimal number with up to 16 digits and 2 decimal places")
    private BigDecimal vbsControl;

    @NotNull(message = "A control value must not be null")
    @Digits(integer = 16, fraction = 2, message = "A control must be a valid decimal number with up to 16 digits and 2 decimal places")
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
    @Size(min = 3, max = 3, message = "Mode must be 3 characters")
    private String mode;

    @Digits(integer = 1, fraction = 2, message = "Crotonaldehyde must be a valid decimal number with up to 1 digit and 2 decimal places")
    private BigDecimal crotonaldehyde;

    @Digits(integer = 1, fraction = 2, message = "Toluene must be a valid decimal number with up to 1 digit and 2 decimal places")
    private BigDecimal toluene;
}
