package ru.centrikt.factorymonitoringservice.application.mappers.fiveminute;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.factorymonitoringservice.domain.enums.Type;
import ru.centrikt.factorymonitoringservice.domain.enums.UnitType;
import ru.centrikt.factorymonitoringservice.domain.models.fiveminute.FiveMinuteProduct;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.fiveminute.ProductRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.fiveminute.FiveMinuteProductResponse;


public class FiveMinuteProductMapper {

    public static FiveMinuteProductResponse toResponse(FiveMinuteProduct entity) {
        if (entity == null) {
            return null;
        }
        FiveMinuteProductResponse dto = FiveMinuteProductResponse.builder().id(entity.getId()).unitType(entity.getUnitType().toString())
                .type(entity.getType().toString()).fullName(entity.getFullName()).shortName(entity.getShortName())
                .alcCode(entity.getAlcCode()).capacity(entity.getCapacity()).alcVolume(entity.getAlcVolume())
                .productVCode(entity.getProductVCode()).build();
        return dto;
    }

    public static FiveMinuteProduct toEntity(ProductRequest productRequest) {
        if (productRequest == null) {
            return null;
        }
        FiveMinuteProduct product = new FiveMinuteProduct();
        return setFields(productRequest, product);
    }

    public static FiveMinuteProduct toEntity(ProductRequest productRequest, FiveMinuteProduct product) {
        if (productRequest == null) {
            return null;
        }
        return setFields(productRequest, product);
    }

    @NotNull
    private static FiveMinuteProduct setFields(ProductRequest productRequest, FiveMinuteProduct product) {
        product.setUnitType(UnitType.fromString(productRequest.getUnitType()));
        product.setType(Type.fromString(productRequest.getType()));
        product.setFullName(productRequest.getFullName());
        product.setShortName(productRequest.getShortName());
        product.setAlcCode(productRequest.getAlcCode());
        product.setCapacity(productRequest.getCapacity());
        product.setAlcVolume(productRequest.getAlcVolume());
        product.setProductVCode(productRequest.getProductVCode());
        return product;
    }
}
