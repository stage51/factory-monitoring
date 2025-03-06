package centrikt.factory_monitoring.five_minute_report.mappers;

import centrikt.factory_monitoring.five_minute_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.five_minute_report.enums.Mode;
import centrikt.factory_monitoring.five_minute_report.enums.Status;
import centrikt.factory_monitoring.five_minute_report.models.Position;

public class PositionMapper {


    public static Position toEntity(PositionRequest positionRequest) {
        if (positionRequest == null) {
            return null;
        }

        Position position = new Position();
        position.setTaxpayerNumber(positionRequest.getTaxpayerNumber());
        position.setControllerNumber(positionRequest.getSensorNumber().split("_")[0]);
        position.setLineNumber(positionRequest.getSensorNumber().split("_")[1]);
        position.setProduct(ProductMapper.toEntity(positionRequest.getProduct()));
        position.setControlDate(positionRequest.getControlDate());
        position.setVbsControl(positionRequest.getVbsControl());
        position.setAControl(positionRequest.getAControl());
        position.setPercentAlc(positionRequest.getPercentAlc());
        position.setBottleCountControl(positionRequest.getBottleCountControl());
        position.setTemperature(positionRequest.getTemperature());
        position.setMode(Mode.fromCode(positionRequest.getMode()));
        position.setStatus(Status.fromDescription(positionRequest.getStatus()));
        return position;
    }

    public static PositionResponse toResponse(Position entity) {
        if (entity == null) {
            return null;
        }

        PositionResponse dto = PositionResponse.builder().id(entity.getId())
                .taxpayerNumber(entity.getTaxpayerNumber()).sensorNumber(entity.getControllerNumber() + "_" + entity.getLineNumber())
                .product(ProductMapper.toResponse(entity.getProduct()))
                .controlDate(entity.getControlDate()).vbsControl(entity.getVbsControl())
                .aControl(entity.getAControl()).percentAlc(entity.getPercentAlc())
                .bottleCountControl(entity.getBottleCountControl()).temperature(entity.getTemperature())
                .mode(entity.getMode().getDescription()).status(entity.getStatus().getDescription()).build();
        return dto;
    }
}
