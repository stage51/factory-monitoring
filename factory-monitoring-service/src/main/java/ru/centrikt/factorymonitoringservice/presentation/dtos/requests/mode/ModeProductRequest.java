package ru.centrikt.factorymonitoringservice.presentation.dtos.requests.mode;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Форма на создание, редактирование продукта")
public class ModeProductRequest {

    @Schema(description = "Тип фасовки продукции: Фасованная, Нефасованная")
    @NotNull(message = "Unit type must not be null")
    private String unitType;

    @Schema(description = "Полное название")
    @NotNull(message = "Full name must not be null")
    @Size(min = 2, max = 255, message = "Full name must be between 2 and 255 characters")
    private String fullName;

    @Schema(description = "Код алкоголя")
    @NotNull(message = "Alcohol code must not be null")
    @Size(min = 1, max = 64, message = "Alcohol code must be between 1 and 64 characters")
    private String alcCode;

    @Schema(description = "Код продукта")
    @NotNull(message = "Product VCode must not be null")
    @Size(min = 1, max = 5, message = "Product VCode must be between 1 and 5 characters")
    private String productVCode;

}

