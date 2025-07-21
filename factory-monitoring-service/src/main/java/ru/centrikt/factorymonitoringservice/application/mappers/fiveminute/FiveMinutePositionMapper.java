package ru.centrikt.factorymonitoringservice.application.mappers.fiveminute;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.factorymonitoringservice.domain.enums.Mode;
import ru.centrikt.factorymonitoringservice.domain.models.fiveminute.FiveMinutePosition;
import ru.centrikt.factorymonitoringservice.domain.models.fiveminute.FiveMinuteProduct;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.fiveminute.FiveMinuteReportPositionRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.fiveminute.FiveMinutePositionResponse;

public class FiveMinutePositionMapper {

    public static FiveMinutePositionResponse toResponse(FiveMinutePosition entity) {
        if (entity == null) {
            return null;
        }

        FiveMinutePositionResponse dto = FiveMinutePositionResponse.builder().id(entity.getId())
                .product(FiveMinuteProductMapper.toResponse(entity.getProduct()))
                .controlDate(entity.getControlDate()).vbsControl(entity.getVbsControl())
                .aControl(entity.getAControl()).percentAlc(entity.getPercentAlc())
                .bottleCountControl(entity.getBottleCountControl()).temperature(entity.getTemperature())
                .mode(entity.getMode().getDescription()).build();
        return dto;
    }

    public static FiveMinutePosition toEntity(FiveMinuteReportPositionRequest fiveMinuteReportPositionRequest) {
        if (fiveMinuteReportPositionRequest == null) {
            return null;
        }
        FiveMinutePosition position = new FiveMinutePosition();
        position.setProduct(FiveMinuteProductMapper.toEntity(fiveMinuteReportPositionRequest.getProduct()));
        return setFields(fiveMinuteReportPositionRequest, position);
    }

    public static FiveMinutePosition toEntity(FiveMinuteReportPositionRequest fiveMinuteReportPositionRequest, FiveMinutePosition position) {
        if (fiveMinuteReportPositionRequest == null) {
            return null;
        }
        position.setProduct(FiveMinuteProductMapper.toEntity(fiveMinuteReportPositionRequest.getProduct()));
        return setFields(fiveMinuteReportPositionRequest, position);
    }

    public static FiveMinutePosition toEntity(FiveMinuteReportPositionRequest fiveMinuteReportPositionRequest, FiveMinutePosition position, FiveMinuteProduct product) {
        if (fiveMinuteReportPositionRequest == null) {
            return null;
        }
        position.setProduct(FiveMinuteProductMapper.toEntity(fiveMinuteReportPositionRequest.getProduct(), product));
        return setFields(fiveMinuteReportPositionRequest, position);
    }

    @NotNull
    private static FiveMinutePosition setFields(FiveMinuteReportPositionRequest fiveMinuteReportPositionRequest, FiveMinutePosition position) {
        position.setControlDate(fiveMinuteReportPositionRequest.getControlDate());
        position.setVbsControl(fiveMinuteReportPositionRequest.getVbsControl());
        position.setAControl(fiveMinuteReportPositionRequest.getAControl());
        position.setPercentAlc(fiveMinuteReportPositionRequest.getPercentAlc());
        position.setBottleCountControl(fiveMinuteReportPositionRequest.getBottleCountControl());
        position.setTemperature(fiveMinuteReportPositionRequest.getTemperature());
        position.setMode(Mode.fromCodeOrDescription(fiveMinuteReportPositionRequest.getMode()));
        return position;
    }
}
