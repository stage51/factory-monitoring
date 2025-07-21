package ru.centrikt.factorymonitoringservice.application.mappers.fiveminute;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.factorymonitoringservice.domain.enums.Type;
import ru.centrikt.factorymonitoringservice.domain.enums.UnitType;
import ru.centrikt.factorymonitoringservice.domain.models.fiveminute.FiveMinuteProduct;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.fiveminute.FiveMinuteProductRequest;
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

    public static FiveMinuteProduct toEntity(FiveMinuteProductRequest fiveMinuteProductRequest) {
        if (fiveMinuteProductRequest == null) {
            return null;
        }
        FiveMinuteProduct product = new FiveMinuteProduct();
        return setFields(fiveMinuteProductRequest, product);
    }

    public static FiveMinuteProduct toEntity(FiveMinuteProductRequest fiveMinuteProductRequest, FiveMinuteProduct product) {
        if (fiveMinuteProductRequest == null) {
            return null;
        }
        return setFields(fiveMinuteProductRequest, product);
    }

    @NotNull
    private static FiveMinuteProduct setFields(FiveMinuteProductRequest fiveMinuteProductRequest, FiveMinuteProduct product) {
        product.setUnitType(UnitType.fromString(fiveMinuteProductRequest.getUnitType()));
        product.setType(Type.fromString(fiveMinuteProductRequest.getType()));
        product.setFullName(fiveMinuteProductRequest.getFullName());
        product.setShortName(fiveMinuteProductRequest.getShortName());
        product.setAlcCode(fiveMinuteProductRequest.getAlcCode());
        product.setCapacity(fiveMinuteProductRequest.getCapacity());
        product.setAlcVolume(fiveMinuteProductRequest.getAlcVolume());
        product.setProductVCode(fiveMinuteProductRequest.getProductVCode());
        return product;
    }
}
