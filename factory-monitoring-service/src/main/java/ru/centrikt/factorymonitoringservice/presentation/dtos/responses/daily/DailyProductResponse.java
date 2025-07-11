package ru.centrikt.factorymonitoringservice.presentation.dtos.responses.daily;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Вывод продукта")
public class DailyProductResponse {
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
