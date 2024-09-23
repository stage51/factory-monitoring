package centrikt.factory_monitoring.daily_report.mappers;

import centrikt.factory_monitoring.daily_report.dtos.ProductDTO;
import centrikt.factory_monitoring.daily_report.enums.Type;
import centrikt.factory_monitoring.daily_report.enums.UnitType;
import centrikt.factory_monitoring.daily_report.models.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        ProductDTO productDTO = new ProductDTO();
        productDTO.setUnitType(product.getUnitType().toString());
        productDTO.setType(product.getType().toString());
        productDTO.setFullName(product.getFullName());
        productDTO.setShortName(product.getShortName());
        productDTO.setAlcCode(product.getAlcCode());
        productDTO.setCapacity(product.getCapacity());
        productDTO.setAlcVolume(product.getAlcVolume());
        productDTO.setProductVCode(product.getProductVCode());
        return productDTO;
    }

    public Product toEntity(ProductDTO productDTO) {
        if (productDTO == null) {
            return null;
        }
        Product product = new Product();
        product.setUnitType(UnitType.fromString(productDTO.getUnitType()));
        product.setType(Type.fromString(productDTO.getType()));
        product.setFullName(productDTO.getFullName());
        product.setShortName(productDTO.getShortName());
        product.setAlcCode(productDTO.getAlcCode());
        product.setCapacity(productDTO.getCapacity());
        product.setAlcVolume(productDTO.getAlcVolume());
        product.setProductVCode(productDTO.getProductVCode());
        return product;
    }
}

