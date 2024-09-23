package centrikt.factory_monitoring.five_minute_report.mappers;

import centrikt.factory_monitoring.five_minute_report.dtos.ProductDTO;
import centrikt.factory_monitoring.five_minute_report.enums.Type;
import centrikt.factory_monitoring.five_minute_report.enums.UnitType;
import centrikt.factory_monitoring.five_minute_report.models.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }
        Product product = new Product();
        product.setUnitType(UnitType.fromString(dto.getUnitType()));
        product.setType(Type.fromString(dto.getType()));
        product.setFullName(dto.getFullName());
        product.setShortName(dto.getShortName());
        product.setAlcCode(dto.getAlcCode());
        product.setCapacity(dto.getCapacity());
        product.setAlcVolume(dto.getAlcVolume());
        product.setProductVCode(dto.getProductVCode());
        product.setCrotonaldehyde(dto.getCrotonaldehyde());
        product.setToluene(dto.getToluene());

        return product;
    }

    public ProductDTO toDTO(Product entity) {
        if (entity == null) {
            return null;
        }
        ProductDTO dto = new ProductDTO();
        dto.setUnitType(entity.getUnitType().toString());
        dto.setType(entity.getType().toString());
        dto.setFullName(entity.getFullName());
        dto.setShortName(entity.getShortName());
        dto.setAlcCode(entity.getAlcCode());
        dto.setCapacity(entity.getCapacity());
        dto.setAlcVolume(entity.getAlcVolume());
        dto.setProductVCode(entity.getProductVCode());
        dto.setCrotonaldehyde(entity.getCrotonaldehyde());
        dto.setToluene(entity.getToluene());

        return dto;
    }
}
