package ru.centrikt.factorymonitoringservice.application.mappers.mode;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.factorymonitoringservice.domain.enums.Mode;
import ru.centrikt.factorymonitoringservice.domain.models.mode.ModePosition;
import ru.centrikt.factorymonitoringservice.domain.models.mode.ModeProduct;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.mode.PositionRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.mode.ModePositionResponse;

public class ModePositionMapper {

    public static ModePositionResponse toResponse(ModePosition position) {
        if (position == null) {
            return null;
        }
        ModePositionResponse dto = ModePositionResponse.builder().id(position.getId()).product(ModeProductMapper.toResponse(position.getProduct()))
                .startDate(position.getStartDate()).endDate(position.getEndDate()).vbsStart(position.getVbsStart()).vbsEnd(position.getVbsEnd())
                .aStart(position.getAStart()).aEnd(position.getAEnd()).percentAlc(position.getPercentAlc())
                .bottleCountStart(position.getBottleCountStart()).bottleCountEnd(position.getBottleCountEnd())
                .temperature(position.getTemperature()).mode(position.getMode().getDescription()).build();
        return dto;
    }

    public static ModePosition toEntity(PositionRequest positionRequest) {
        if (positionRequest == null) {
            return null;
        }
        ModePosition position = new ModePosition();
        position.setProduct(ModeProductMapper.toEntity(positionRequest.getProduct()));
        return setFields(positionRequest, position);
    }

    public static ModePosition toEntity(PositionRequest positionRequest, ModePosition position) {
        if (positionRequest == null) {
            return null;
        }
        position.setProduct(ModeProductMapper.toEntity(positionRequest.getProduct()));
        return setFields(positionRequest, position);
    }

    public static ModePosition toEntity(PositionRequest positionRequest, ModePosition position, ModeProduct product) {
        if (positionRequest == null) {
            return null;
        }
        position.setProduct(ModeProductMapper.toEntity(positionRequest.getProduct(), product));
        return setFields(positionRequest, position);
    }

    @NotNull
    private static ModePosition setFields(PositionRequest positionRequest, ModePosition position) {
        position.setStartDate(positionRequest.getStartDate());
        position.setEndDate(positionRequest.getEndDate());
        position.setVbsStart(positionRequest.getVbsStart());
        position.setVbsEnd(positionRequest.getVbsEnd());
        position.setAStart(positionRequest.getAStart());
        position.setAEnd(positionRequest.getAEnd());
        position.setPercentAlc(positionRequest.getPercentAlc());
        position.setBottleCountStart(positionRequest.getBottleCountStart());
        position.setBottleCountEnd(positionRequest.getBottleCountEnd());
        position.setTemperature(positionRequest.getTemperature());
        position.setMode(Mode.fromCodeOrDescription(positionRequest.getMode()));
        return position;
    }
}

