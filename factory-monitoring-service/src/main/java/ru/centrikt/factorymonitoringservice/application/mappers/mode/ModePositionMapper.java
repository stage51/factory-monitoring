package ru.centrikt.factorymonitoringservice.application.mappers.mode;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.factorymonitoringservice.domain.enums.Mode;
import ru.centrikt.factorymonitoringservice.domain.models.mode.ModePosition;
import ru.centrikt.factorymonitoringservice.domain.models.mode.ModeProduct;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.mode.ModePositionRequest;
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

    public static ModePosition toEntity(ModePositionRequest modePositionRequest) {
        if (modePositionRequest == null) {
            return null;
        }
        ModePosition position = new ModePosition();
        position.setProduct(ModeProductMapper.toEntity(modePositionRequest.getProduct()));
        return setFields(modePositionRequest, position);
    }

    public static ModePosition toEntity(ModePositionRequest modePositionRequest, ModePosition position) {
        if (modePositionRequest == null) {
            return null;
        }
        position.setProduct(ModeProductMapper.toEntity(modePositionRequest.getProduct()));
        return setFields(modePositionRequest, position);
    }

    public static ModePosition toEntity(ModePositionRequest modePositionRequest, ModePosition position, ModeProduct product) {
        if (modePositionRequest == null) {
            return null;
        }
        position.setProduct(ModeProductMapper.toEntity(modePositionRequest.getProduct(), product));
        return setFields(modePositionRequest, position);
    }

    @NotNull
    private static ModePosition setFields(ModePositionRequest modePositionRequest, ModePosition position) {
        position.setStartDate(modePositionRequest.getStartDate());
        position.setEndDate(modePositionRequest.getEndDate());
        position.setVbsStart(modePositionRequest.getVbsStart());
        position.setVbsEnd(modePositionRequest.getVbsEnd());
        position.setAStart(modePositionRequest.getAStart());
        position.setAEnd(modePositionRequest.getAEnd());
        position.setPercentAlc(modePositionRequest.getPercentAlc());
        position.setBottleCountStart(modePositionRequest.getBottleCountStart());
        position.setBottleCountEnd(modePositionRequest.getBottleCountEnd());
        position.setTemperature(modePositionRequest.getTemperature());
        position.setMode(Mode.fromCodeOrDescription(modePositionRequest.getMode()));
        return position;
    }
}

