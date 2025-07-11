package ru.centrikt.factorymonitoringservice.presentation.dtos.responses.mode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Вывод продукта")
public class ModeProductResponse {

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
