package centrikt.factorymonitoring.modereport.mappers;


import centrikt.factorymonitoring.modereport.dtos.requests.PositionRequest;
import centrikt.factorymonitoring.modereport.dtos.requests.ProductRequest;
import centrikt.factorymonitoring.modereport.dtos.responses.ProductResponse;
import centrikt.factorymonitoring.modereport.enums.Mode;
import centrikt.factorymonitoring.modereport.enums.Status;
import centrikt.factorymonitoring.modereport.enums.UnitType;
import centrikt.factorymonitoring.modereport.models.Position;
import centrikt.factorymonitoring.modereport.models.Product;
import jakarta.validation.constraints.NotNull;

public class ProductMapper {

    public static ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        ProductResponse dto = ProductResponse.builder().id(product.getId()).unitType(product.getUnitType().toString())
               .fullName(product.getFullName()).alcCode(product.getAlcCode()).productVCode(product.getProductVCode()).build();
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
        product.setFullName(productRequest.getFullName());
        product.setAlcCode(productRequest.getAlcCode());
        product.setProductVCode(productRequest.getProductVCode());
        return product;
    }
}

