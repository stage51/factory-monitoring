package ru.centrikt.factorymonitoringservice.application.mappers.daily;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.factorymonitoringservice.domain.enums.Mode;
import ru.centrikt.factorymonitoringservice.domain.models.daily.DailyPosition;
import ru.centrikt.factorymonitoringservice.domain.models.daily.Product;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.daily.PositionRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.daily.DailyPositionResponse;

public class DailyPositionMapper {

    public static DailyPositionResponse toResponse(DailyPosition position) {
        if (position == null) {
            return null;
        }
        DailyPositionResponse dto = DailyPositionResponse.builder().id(position.getId()).product(DailyProductMapper.toResponse(position.getProduct()))
                .startDate(position.getStartDate()).endDate(position.getEndDate()).vbsStart(position.getVbsStart()).vbsEnd(position.getVbsEnd())
                .aStart(position.getAStart()).aEnd(position.getAEnd()).percentAlc(position.getPercentAlc())
                .bottleCountStart(position.getBottleCountStart()).bottleCountEnd(position.getBottleCountEnd())
                .temperature(position.getTemperature()).mode(position.getMode().getDescription()).build();
        return dto;
    }

    public static DailyPosition toEntity(PositionRequest positionRequest) {
        if (positionRequest == null) {
            return null;
        }
        DailyPosition position = new DailyPosition();
        position.setProduct(DailyProductMapper.toEntity(positionRequest.getProduct()));
        return setFields(positionRequest, position);
    }

    public static DailyPosition toEntity(PositionRequest positionRequest, DailyPosition position) {
        if (positionRequest == null) {
            return null;
        }
        position.setProduct(DailyProductMapper.toEntity(positionRequest.getProduct()));
        return setFields(positionRequest, position);
    }

    public static DailyPosition toEntity(PositionRequest positionRequest, DailyPosition position, Product product) {
        if (positionRequest == null) {
            return null;
        }
        position.setProduct(DailyProductMapper.toEntity(positionRequest.getProduct(), product));
        return setFields(positionRequest, position);
    }

    @NotNull
    private static DailyPosition setFields(PositionRequest positionRequest, DailyPosition position) {
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

