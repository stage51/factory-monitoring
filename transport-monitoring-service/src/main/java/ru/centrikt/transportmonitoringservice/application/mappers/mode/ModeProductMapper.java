package ru.centrikt.transportmonitoringservice.application.mappers.mode;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.transportmonitoringservice.domain.enums.UnitType;
import ru.centrikt.transportmonitoringservice.domain.models.mode.ModeProduct;
import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.mode.ModeProductRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.mode.ModeProductResponse;

public class ModeProductMapper {

    public static ModeProductResponse toResponse(ModeProduct product) {
        if (product == null) {
            return null;
        }
        ModeProductResponse dto = ModeProductResponse.builder().id(product.getId()).unitType(product.getUnitType().toString())
               .fullName(product.getFullName()).alcCode(product.getAlcCode()).productVCode(product.getProductVCode()).build();
        return dto;
    }

    public static ModeProduct toEntity(ModeProductRequest modeProductRequest) {
        if (modeProductRequest == null) {
            return null;
        }
        ModeProduct product = new ModeProduct();
        return setFields(modeProductRequest, product);
    }

    public static ModeProduct toEntity(ModeProductRequest modeProductRequest, ModeProduct product) {
        if (modeProductRequest == null) {
            return null;
        }
        return setFields(modeProductRequest, product);
    }

    @NotNull
    private static ModeProduct setFields(ModeProductRequest modeProductRequest, ModeProduct product) {
        product.setUnitType(UnitType.fromString(modeProductRequest.getUnitType()));
        product.setFullName(modeProductRequest.getFullName());
        product.setAlcCode(modeProductRequest.getAlcCode());
        product.setProductVCode(modeProductRequest.getProductVCode());
        return product;
    }
}

