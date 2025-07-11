package ru.centrikt.factorymonitoringservice.application.mappers.mode;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.factorymonitoringservice.domain.enums.UnitType;
import ru.centrikt.factorymonitoringservice.domain.models.mode.ModeProduct;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.mode.ProductRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.mode.ModeProductResponse;

public class ModeProductMapper {

    public static ModeProductResponse toResponse(ModeProduct product) {
        if (product == null) {
            return null;
        }
        ModeProductResponse dto = ModeProductResponse.builder().id(product.getId()).unitType(product.getUnitType().toString())
               .fullName(product.getFullName()).alcCode(product.getAlcCode()).productVCode(product.getProductVCode()).build();
        return dto;
    }

    public static ModeProduct toEntity(ProductRequest productRequest) {
        if (productRequest == null) {
            return null;
        }
        ModeProduct product = new ModeProduct();
        return setFields(productRequest, product);
    }

    public static ModeProduct toEntity(ProductRequest productRequest, ModeProduct product) {
        if (productRequest == null) {
            return null;
        }
        return setFields(productRequest, product);
    }

    @NotNull
    private static ModeProduct setFields(ProductRequest productRequest, ModeProduct product) {
        product.setUnitType(UnitType.fromString(productRequest.getUnitType()));
        product.setFullName(productRequest.getFullName());
        product.setAlcCode(productRequest.getAlcCode());
        product.setProductVCode(productRequest.getProductVCode());
        return product;
    }
}

