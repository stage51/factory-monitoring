package centrikt.factory_monitoring.daily_report.mappers;

import centrikt.factory_monitoring.daily_report.dtos.requests.ProductRequest;
import centrikt.factory_monitoring.daily_report.dtos.responses.ProductResponse;
import centrikt.factory_monitoring.daily_report.enums.Type;
import centrikt.factory_monitoring.daily_report.enums.UnitType;
import centrikt.factory_monitoring.daily_report.models.Product;

public class ProductMapper {

    public static ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        ProductResponse dto = ProductResponse.builder().id(product.getId()).unitType(product.getUnitType().toString())
                .type(product.getType().toString()).fullName(product.getFullName()).shortName(product.getShortName())
                .alcCode(product.getAlcCode()).capacity(product.getCapacity()).alcVolume(product.getAlcVolume())
                .productVCode(product.getProductVCode()).build();
        return dto;
    }

    public static Product toEntity(ProductRequest productRequest) {
        if (productRequest == null) {
            return null;
        }
        Product product = new Product();
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

