package ru.centrikt.factorymonitoringservice.application.mappers.daily;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.factorymonitoringservice.domain.enums.Mode;
import ru.centrikt.factorymonitoringservice.domain.models.daily.DailyPosition;
import ru.centrikt.factorymonitoringservice.domain.models.daily.DailyProduct;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.daily.DailyReportPositionRequest;
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

    public static DailyPosition toEntity(DailyReportPositionRequest dailyReportPositionRequest) {
        if (dailyReportPositionRequest == null) {
            return null;
        }
        DailyPosition position = new DailyPosition();
        position.setProduct(DailyProductMapper.toEntity(dailyReportPositionRequest.getProduct()));
        return setFields(dailyReportPositionRequest, position);
    }

    public static DailyPosition toEntity(DailyReportPositionRequest dailyReportPositionRequest, DailyPosition position) {
        if (dailyReportPositionRequest == null) {
            return null;
        }
        position.setProduct(DailyProductMapper.toEntity(dailyReportPositionRequest.getProduct()));
        return setFields(dailyReportPositionRequest, position);
    }

    public static DailyPosition toEntity(DailyReportPositionRequest dailyReportPositionRequest, DailyPosition position, DailyProduct product) {
        if (dailyReportPositionRequest == null) {
            return null;
        }
        position.setProduct(DailyProductMapper.toEntity(dailyReportPositionRequest.getProduct(), product));
        return setFields(dailyReportPositionRequest, position);
    }

    @NotNull
    private static DailyPosition setFields(DailyReportPositionRequest dailyReportPositionRequest, DailyPosition position) {
        position.setStartDate(dailyReportPositionRequest.getStartDate());
        position.setEndDate(dailyReportPositionRequest.getEndDate());
        position.setVbsStart(dailyReportPositionRequest.getVbsStart());
        position.setVbsEnd(dailyReportPositionRequest.getVbsEnd());
        position.setAStart(dailyReportPositionRequest.getAStart());
        position.setAEnd(dailyReportPositionRequest.getAEnd());
        position.setPercentAlc(dailyReportPositionRequest.getPercentAlc());
        position.setBottleCountStart(dailyReportPositionRequest.getBottleCountStart());
        position.setBottleCountEnd(dailyReportPositionRequest.getBottleCountEnd());
        position.setTemperature(dailyReportPositionRequest.getTemperature());
        position.setMode(Mode.fromCodeOrDescription(dailyReportPositionRequest.getMode()));
        return position;
    }
}

