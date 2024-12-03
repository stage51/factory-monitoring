package centrikt.factory_monitoring.five_minute_report.mappers;

import centrikt.factory_monitoring.five_minute_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.five_minute_report.enums.Mode;
import centrikt.factory_monitoring.five_minute_report.enums.Status;
import centrikt.factory_monitoring.five_minute_report.models.Position;

public class PositionMapper {


    public static Position toEntity(PositionRequest dto) {
        if (dto == null) {
            return null;
        }

        Position position = new Position();
        position.setTaxpayerNumber(dto.getTaxpayerNumber());
        position.setProduct(ProductMapper.toEntity(dto.getProduct()));
        position.setControlDate(dto.getControlDate());
        position.setVbsControl(dto.getVbsControl());
        position.setAControl(dto.getAControl());
        position.setPercentAlc(dto.getPercentAlc());
        position.setBottleCountControl(dto.getBottleCountControl());
        position.setTemperature(dto.getTemperature());
        position.setMode(Mode.fromDescription(dto.getMode()));
        position.setStatus(Status.fromDescription(dto.getStatus()));
        return position;
    }

    public static PositionResponse toResponse(Position entity) {
        if (entity == null) {
            return null;
        }

        PositionResponse dto = PositionResponse.builder().id(entity.getId()).taxpayerNumber(entity.getTaxpayerNumber())
                .product(ProductMapper.toResponse(entity.getProduct()))
                .controlDate(entity.getControlDate()).vbsControl(entity.getVbsControl())
                .aControl(entity.getAControl()).percentAlc(entity.getPercentAlc())
                .bottleCountControl(entity.getBottleCountControl()).temperature(entity.getTemperature())
                .mode(entity.getMode().toString()).status(entity.getStatus().toString()).build();

        return dto;
    }
}
