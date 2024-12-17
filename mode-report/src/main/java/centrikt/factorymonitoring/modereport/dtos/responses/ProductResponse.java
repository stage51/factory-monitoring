package centrikt.factorymonitoring.modereport.dtos.responses;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JacksonXmlRootElement(localName = "product")
@Schema(description = "Вывод продукта")
public class ProductResponse {

    private Long id;

    @Schema(description = "Тип фасовки продукции: Фасованная, Нефасованная")
    private String unitType;

    @Schema(description = "Полное название")
    private String fullName;

    @Schema(description = "Код алкоголя")
    private String alcCode;

    @Schema(description = "Код продукта")
    private String productVCode;
}
