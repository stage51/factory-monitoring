package centrikt.factory_monitoring.daily_report.mappers;

import centrikt.factory_monitoring.daily_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.daily_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.daily_report.enums.Mode;
import centrikt.factory_monitoring.daily_report.enums.Status;
import centrikt.factory_monitoring.daily_report.models.Position;
import centrikt.factory_monitoring.daily_report.models.Product;
import jakarta.validation.constraints.NotNull;

public class PositionMapper {

    public static PositionResponse toResponse(Position position) {
        if (position == null) {
            return null;
        }
        PositionResponse dto = PositionResponse.builder().id(position.getId()).sensorNumber(position.getControllerNumber() + "_" + position.getLineNumber())
                .taxpayerNumber(position.getTaxpayerNumber()).product(ProductMapper.toResponse(position.getProduct()))
                .startDate(position.getStartDate()).endDate(position.getEndDate()).vbsStart(position.getVbsStart()).vbsEnd(position.getVbsEnd())
                .aStart(position.getAStart()).aEnd(position.getAEnd()).percentAlc(position.getPercentAlc())
                .bottleCountStart(position.getBottleCountStart()).bottleCountEnd(position.getBottleCountEnd())
                .temperature(position.getTemperature()).mode(position.getMode().getDescription()).status(position.getStatus().getDescription()).build();
        return dto;
    }

    public static Position toEntity(PositionRequest positionRequest) {
        if (positionRequest == null) {
            return null;
        }
        Position position = new Position();
        position.setProduct(ProductMapper.toEntity(positionRequest.getProduct()));
        return setFields(positionRequest, position);
    }

    public static Position toEntity(PositionRequest positionRequest, Position position) {
        if (positionRequest == null) {
            return null;
        }
        position.setProduct(ProductMapper.toEntity(positionRequest.getProduct()));
        return setFields(positionRequest, position);
    }

    public static Position toEntity(PositionRequest positionRequest, Position position, Product product) {
        if (positionRequest == null) {
            return null;
        }
        position.setProduct(ProductMapper.toEntity(positionRequest.getProduct(), product));
        return setFields(positionRequest, position);
    }

    @NotNull
    private static Position setFields(PositionRequest positionRequest, Position position) {
        position.setTaxpayerNumber(positionRequest.getTaxpayerNumber());
        position.setControllerNumber(positionRequest.getSensorNumber().split("_")[0]);
        position.setLineNumber(positionRequest.getSensorNumber().split("_")[1]);
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
        position.setStatus(Status.fromDescription(positionRequest.getStatus()));
        return position;
    }
}

