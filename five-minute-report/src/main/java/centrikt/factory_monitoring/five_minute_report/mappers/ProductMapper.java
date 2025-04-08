package centrikt.factory_monitoring.five_minute_report.mappers;

import centrikt.factory_monitoring.five_minute_report.dtos.requests.ProductRequest;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.ProductResponse;
import centrikt.factory_monitoring.five_minute_report.enums.Type;
import centrikt.factory_monitoring.five_minute_report.enums.UnitType;
import centrikt.factory_monitoring.five_minute_report.models.Product;
import jakarta.validation.constraints.NotNull;
import org.aspectj.weaver.Position;


public class ProductMapper {

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

    public static Product toEntity(ProductRequest productRequest) {
        if (productRequest == null) {
            return null;
        }
        Product product = new Product();
        return setFields(productRequest, product);
    }

    public static Product toEntity(ProductRequest productRequest, Product product) {
        if (productRequest == null) {
            return null;
        }
        return setFields(productRequest, product);
    }

    @NotNull
    private static Product setFields(ProductRequest productRequest, Product product) {
        product.setUnitType(UnitType.fromString(productRequest.getUnitType()));
        product.setType(Type.fromString(productRequest.getType()));
        product.setFullName(productRequest.getFullName());
        product.setShortName(productRequest.getShortName());
        product.setAlcCode(productRequest.getAlcCode());
        product.setCapacity(productRequest.getCapacity());
        product.setAlcVolume(productRequest.getAlcVolume());
        product.setProductVCode(productRequest.getProductVCode());
        return product;
    }
}
