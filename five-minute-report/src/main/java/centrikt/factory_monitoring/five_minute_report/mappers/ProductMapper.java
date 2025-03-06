package centrikt.factory_monitoring.five_minute_report.mappers;

import centrikt.factory_monitoring.five_minute_report.dtos.requests.ProductRequest;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.ProductResponse;
import centrikt.factory_monitoring.five_minute_report.enums.Type;
import centrikt.factory_monitoring.five_minute_report.enums.UnitType;
import centrikt.factory_monitoring.five_minute_report.models.Product;
import org.aspectj.weaver.Position;


public class ProductMapper {
    public static Product toEntity(ProductRequest dto) {
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

        return product;
    }

    public static ProductResponse toResponse(Product entity) {
        if (entity == null) {
            return null;
        }
        ProductResponse dto = ProductResponse.builder().id(entity.getId()).unitType(entity.getUnitType().toString())
                .type(entity.getType().toString()).fullName(entity.getFullName()).shortName(entity.getShortName())
                .alcCode(entity.getAlcCode()).capacity(entity.getCapacity()).alcVolume(entity.getAlcVolume())
                .productVCode(entity.getProductVCode()).build();
        return dto;
    }
}
