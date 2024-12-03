package centrikt.factorymonitoring.modereport.mappers;


import centrikt.factorymonitoring.modereport.dtos.requests.ProductRequest;
import centrikt.factorymonitoring.modereport.dtos.responses.ProductResponse;
import centrikt.factorymonitoring.modereport.enums.Type;
import centrikt.factorymonitoring.modereport.enums.UnitType;
import centrikt.factorymonitoring.modereport.models.Product;

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
        product.setUnitType(UnitType.fromString(productRequest.getUnitType()));
        product.setFullName(productRequest.getFullName());
        product.setAlcCode(productRequest.getAlcCode());
        product.setProductVCode(productRequest.getProductVCode());
        return product;
    }
}

