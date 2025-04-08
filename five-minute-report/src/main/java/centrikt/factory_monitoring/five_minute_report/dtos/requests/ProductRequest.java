package centrikt.factory_monitoring.five_minute_report.dtos.requests;

import java.math.BigDecimal;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "product")
@Schema(description = "Форма на создание, редактирование продукта")
public class ProductRequest {

    @Schema(description = "Тип фасовки продукции: Фасованная, Нефасованная")
    @NotNull(message = "Unit type must not be null")
    private String unitType;

    @Schema(description = "Тип продукции: Алкогольная продукция, Спиртосодержащая пищевая продукция, " +
            "Спиртосодержащая непищевая продукция, Этиловый спирт")
    @NotNull(message = "Type must not be null")
    private String type;

    @Schema(description = "Полное название")
    @NotNull(message = "Full name must not be null")
    @Size(min = 2, max = 255, message = "Full name must be between 2 and 255 characters")
    private String fullName;

    @Schema(description = "Краткое название")
    @NotNull(message = "Short name must not be null")
    @Size(min = 2, max = 64, message = "Short name must be between 2 and 64 characters")
    private String shortName;

    @Schema(description = "Код алкоголя")
    @NotNull(message = "Alcohol code must not be null")
    @Size(min = 1, max = 64, message = "Alcohol code must be between 1 and 64 characters")
    private String alcCode;

    @Schema(description = "Емкость")
    @Digits(integer = 12, fraction = 3, message = "Capacity must be a valid decimal number with up to 12 digits and 3 decimal places")
    private BigDecimal capacity;

    @Schema(description = "Алкогольный объем")
    @NotNull(message = "Alcohol volume must not be null")
    @Digits(integer = 12, fraction = 3, message = "Alcohol volume must be a valid decimal number with up to 12 digits and 3 decimal places")
    private BigDecimal alcVolume;

    @Schema(description = "Код продукта")
    @NotNull(message = "Product VCode must not be null")
    @Size(min = 1, max = 5, message = "Product VCode must be between 1 and 5 characters")
    private String productVCode;

}

