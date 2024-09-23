package centrikt.factory_monitoring.five_minute_report.mappers;

import centrikt.factory_monitoring.five_minute_report.dtos.PositionDTO;
import centrikt.factory_monitoring.five_minute_report.enums.Mode;
import centrikt.factory_monitoring.five_minute_report.models.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PositionMapper {

    private ProductMapper productMapper;

    public PositionMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }
    @Autowired
    public void setProductMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public Position toEntity(PositionDTO dto) {
        if (dto == null) {
            return null;
        }

        Position position = new Position();
        position.setProduct(productMapper.toEntity(dto.getProduct()));
        position.setControlDate(dto.getControlDate());
        position.setVbsControl(dto.getVbsControl());
        position.setAControl(dto.getAControl());
        position.setPercentAlc(dto.getPercentAlc());
        position.setBottleCountControl(dto.getBottleCountControl());
        position.setTemperature(dto.getTemperature());
        position.setMode(Mode.fromCode(dto.getMode()));

        return position;
    }

    public PositionDTO toDTO(Position entity) {
        if (entity == null) {
            return null;
        }

        PositionDTO dto = new PositionDTO();
        dto.setProduct(productMapper.toDTO(entity.getProduct()));
        dto.setControlDate(entity.getControlDate());
        dto.setVbsControl(entity.getVbsControl());
        dto.setAControl(entity.getAControl());
        dto.setPercentAlc(entity.getPercentAlc());
        dto.setBottleCountControl(entity.getBottleCountControl());
        dto.setTemperature(entity.getTemperature());
        dto.setMode(entity.getMode().toString());

        return dto;
    }
}
