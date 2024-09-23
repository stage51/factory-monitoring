package centrikt.factory_monitoring.daily_report.mappers;

import centrikt.factory_monitoring.daily_report.dtos.PositionDTO;
import centrikt.factory_monitoring.daily_report.enums.Mode;
import centrikt.factory_monitoring.daily_report.models.Position;
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

    public PositionDTO toDTO(Position position) {
        if (position == null) {
            return null;
        }
        PositionDTO positionDTO = new PositionDTO();
        positionDTO.setProduct(productMapper.toDTO(position.getProduct()));
        positionDTO.setStartDate(position.getStartDate());
        positionDTO.setEndDate(position.getEndDate());
        positionDTO.setVbsStart(position.getVbsStart());
        positionDTO.setVbsEnd(position.getVbsEnd());
        positionDTO.setAStart(position.getAStart());
        positionDTO.setAEnd(position.getAEnd());
        positionDTO.setPercentAlc(position.getPercentAlc());
        positionDTO.setBottleCountStart(position.getBottleCountStart());
        positionDTO.setBottleCountEnd(position.getBottleCountEnd());
        positionDTO.setTemperature(position.getTemperature());
        positionDTO.setMode(position.getMode().toString());
        positionDTO.setCrotonaldehyde(position.getCrotonaldehyde());
        positionDTO.setToluene(position.getToluene());
        return positionDTO;
    }

    public Position toEntity(PositionDTO positionDTO) {
        if (positionDTO == null) {
            return null;
        }
        Position position = new Position();
        position.setProduct(productMapper.toEntity(positionDTO.getProduct()));
        position.setStartDate(positionDTO.getStartDate());
        position.setEndDate(positionDTO.getEndDate());
        position.setVbsStart(positionDTO.getVbsStart());
        position.setVbsEnd(positionDTO.getVbsEnd());
        position.setAStart(positionDTO.getAStart());
        position.setAEnd(positionDTO.getAEnd());
        position.setPercentAlc(positionDTO.getPercentAlc());
        position.setBottleCountStart(positionDTO.getBottleCountStart());
        position.setBottleCountEnd(positionDTO.getBottleCountEnd());
        position.setTemperature(positionDTO.getTemperature());
        position.setMode(Mode.fromCode(positionDTO.getMode()));
        position.setCrotonaldehyde(positionDTO.getCrotonaldehyde());
        position.setToluene(positionDTO.getToluene());
        return position;
    }
}

