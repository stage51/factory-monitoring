package centrikt.factory_monitoring.daily_report.dtos.responses;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JacksonXmlRootElement(localName = "product")
@Builder
@Schema(description = "Вывод продукта")
public class ProductResponse {
    private Long id;

    @Schema(description = "Тип фасовки продукции: Фасованная, Нефасованная")
    private String unitType;

    @Schema(description = "Тип продукции: Алкогольная продукция, Спиртосодержащая пищевая продукция, " +
            "Спиртосодержащая непищевая продукция, Этиловый спирт")
    private String type;

    @Schema(description = "Полное название")
    private String fullName;

    @Schema(description = "Краткое название")
    private String shortName;

    @Schema(description = "Код алкоголя")
    private String alcCode;

    @Schema(description = "Емкость")
    private BigDecimal capacity;

    @Schema(description = "Алкогольный объем")
    private BigDecimal alcVolume;

    @Schema(description = "Код продукта")
    private String productVCode;
}
