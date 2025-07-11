package ru.centrikt.factorymonitoringservice.application.mappers.fiveminute;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.factorymonitoringservice.domain.enums.Mode;
import ru.centrikt.factorymonitoringservice.domain.models.fiveminute.FiveMinutePosition;
import ru.centrikt.factorymonitoringservice.domain.models.fiveminute.FiveMinuteProduct;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.fiveminute.PositionRequest;
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

    public static FiveMinutePosition toEntity(PositionRequest positionRequest) {
        if (positionRequest == null) {
            return null;
        }
        FiveMinutePosition position = new FiveMinutePosition();
        position.setProduct(FiveMinuteProductMapper.toEntity(positionRequest.getProduct()));
        return setFields(positionRequest, position);
    }

    public static FiveMinutePosition toEntity(PositionRequest positionRequest, FiveMinutePosition position) {
        if (positionRequest == null) {
            return null;
        }
        position.setProduct(FiveMinuteProductMapper.toEntity(positionRequest.getProduct()));
        return setFields(positionRequest, position);
    }

    public static FiveMinutePosition toEntity(PositionRequest positionRequest, FiveMinutePosition position, FiveMinuteProduct product) {
        if (positionRequest == null) {
            return null;
        }
        position.setProduct(FiveMinuteProductMapper.toEntity(positionRequest.getProduct(), product));
        return setFields(positionRequest, position);
    }

    @NotNull
    private static FiveMinutePosition setFields(PositionRequest positionRequest, FiveMinutePosition position) {
        position.setControlDate(positionRequest.getControlDate());
        position.setVbsControl(positionRequest.getVbsControl());
        position.setAControl(positionRequest.getAControl());
        position.setPercentAlc(positionRequest.getPercentAlc());
        position.setBottleCountControl(positionRequest.getBottleCountControl());
        position.setTemperature(positionRequest.getTemperature());
        position.setMode(Mode.fromCodeOrDescription(positionRequest.getMode()));
        return position;
    }
}
