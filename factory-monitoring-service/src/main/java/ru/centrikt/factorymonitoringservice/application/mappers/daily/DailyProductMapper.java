package ru.centrikt.factorymonitoringservice.application.mappers.daily;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.factorymonitoringservice.domain.enums.Type;
import ru.centrikt.factorymonitoringservice.domain.enums.UnitType;
import ru.centrikt.factorymonitoringservice.domain.models.daily.DailyProduct;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.daily.DailyReportProductRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.daily.DailyProductResponse;

public class DailyProductMapper {

    public static DailyProductResponse toResponse(DailyProduct product) {
        if (product == null) {
            return null;
        }
        DailyProductResponse dto = DailyProductResponse.builder().id(product.getId()).unitType(product.getUnitType().toString())
                .type(product.getType().toString()).fullName(product.getFullName()).shortName(product.getShortName())
                .alcCode(product.getAlcCode()).capacity(product.getCapacity()).alcVolume(product.getAlcVolume())
                .productVCode(product.getProductVCode()).build();
        return dto;
    }

    public static DailyProduct toEntity(DailyReportProductRequest dailyReportProductRequest) {
        if (dailyReportProductRequest == null) {
            return null;
        }
        DailyProduct product = new DailyProduct();
        return setFields(dailyReportProductRequest, product);
    }

    public static DailyProduct toEntity(DailyReportProductRequest dailyReportProductRequest, DailyProduct product) {
        if (dailyReportProductRequest == null) {
            return null;
        }
        return setFields(dailyReportProductRequest, product);
    }

    @NotNull
    private static DailyProduct setFields(DailyReportProductRequest dailyReportProductRequest, DailyProduct product) {
        product.setUnitType(UnitType.fromString(dailyReportProductRequest.getUnitType()));
        product.setType(Type.fromString(dailyReportProductRequest.getType()));
        product.setFullName(dailyReportProductRequest.getFullName());
        product.setShortName(dailyReportProductRequest.getShortName());
        product.setAlcCode(dailyReportProductRequest.getAlcCode());
        product.setCapacity(dailyReportProductRequest.getCapacity());
        product.setAlcVolume(dailyReportProductRequest.getAlcVolume());
        product.setProductVCode(dailyReportProductRequest.getProductVCode());
        return product;
    }
}

