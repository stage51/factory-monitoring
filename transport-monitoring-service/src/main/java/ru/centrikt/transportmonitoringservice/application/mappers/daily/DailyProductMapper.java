package ru.centrikt.transportmonitoringservice.application.mappers.daily;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.transportmonitoringservice.domain.enums.UnitType;
import ru.centrikt.transportmonitoringservice.domain.models.daily.DailyProduct;
import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.daily.DailyReportProductRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.daily.DailyProductResponse;

public class DailyProductMapper {

    public static DailyProductResponse toResponse(DailyProduct product) {
        if (product == null) {
            return null;
        }
        DailyProductResponse dto = DailyProductResponse.builder()
                .id(product.getId())
                .unitType(product.getUnitType().toString())
                .fullName(product.getFullName())
                .alcCode(product.getAlcCode())
                .alcVolume(product.getAlcVolume())
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
        product.setFullName(dailyReportProductRequest.getFullName());
        product.setAlcCode(dailyReportProductRequest.getAlcCode());
        product.setAlcVolume(dailyReportProductRequest.getAlcVolume());
        product.setProductVCode(dailyReportProductRequest.getProductVCode());
        return product;
    }
}

