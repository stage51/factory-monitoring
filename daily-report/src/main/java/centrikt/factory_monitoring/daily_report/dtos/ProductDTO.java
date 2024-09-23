package centrikt.factory_monitoring.daily_report.dtos;

import centrikt.factory_monitoring.daily_report.enums.Type;
import centrikt.factory_monitoring.daily_report.enums.UnitType;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JacksonXmlRootElement(localName = "product")
public class ProductDTO {

    @NotNull(message = "Unit type must not be null")
    private String unitType;

    @NotNull(message = "Type must not be null")
    private String type;

    @NotNull(message = "Full name must not be null")
    @Size(min = 2, max = 255, message = "Full name must be between 2 and 255 characters")
    private String fullName;

    @NotNull(message = "Short name must not be null")
    @Size(min = 2, max = 64, message = "Short name must be between 2 and 64 characters")
    private String shortName;

    @NotNull(message = "Alcohol code must not be null")
    @Size(min = 1, max = 64, message = "Alcohol code must be between 1 and 64 characters")
    private String alcCode;

    @Digits(integer = 12, fraction = 3, message = "Capacity must be a valid decimal number with up to 12 digits and 3 decimal places")
    private BigDecimal capacity;

    @NotNull(message = "Alcohol volume must not be null")
    @Digits(integer = 12, fraction = 3, message = "Alcohol volume must be a valid decimal number with up to 12 digits and 3 decimal places")
    private BigDecimal alcVolume;

    @NotNull(message = "Product VCode must not be null")
    @Size(min = 1, max = 5, message = "Product VCode must be between 1 and 5 characters")
    private String productVCode;

}

