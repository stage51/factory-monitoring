package ru.centrikt.factorymonitoringservice.application.mappers.daily;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.factorymonitoringservice.domain.enums.Type;
import ru.centrikt.factorymonitoringservice.domain.enums.UnitType;
import ru.centrikt.factorymonitoringservice.domain.models.daily.Product;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.daily.ProductRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.daily.DailyProductResponse;

public class DailyProductMapper {

    public static DailyProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        DailyProductResponse dto = DailyProductResponse.builder().id(product.getId()).unitType(product.getUnitType().toString())
                .type(product.getType().toString()).fullName(product.getFullName()).shortName(product.getShortName())
                .alcCode(product.getAlcCode()).capacity(product.getCapacity()).alcVolume(product.getAlcVolume())
                .productVCode(product.getProductVCode()).build();
        return dto;
    }

    public static Product toEntity(ProductRequest productRequest) {
        if (productRequest == null) {
            return null;
        }
        Product product = new Product();
        return setFields(productRequest, product);
    }

    public static Product toEntity(ProductRequest productRequest, Product product) {
        if (productRequest == null) {
            return null;
        }
        return setFields(productRequest, product);
    }

    @NotNull
    private static Product setFields(ProductRequest productRequest, Product product) {
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

