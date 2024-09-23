package centrikt.factory_monitoring.daily_report.services.impl;

import centrikt.factory_monitoring.daily_report.dtos.ProductDTO;
import centrikt.factory_monitoring.daily_report.exceptions.EntityNotFoundException;
import centrikt.factory_monitoring.daily_report.mappers.ProductMapper;
import centrikt.factory_monitoring.daily_report.models.Product;
import centrikt.factory_monitoring.daily_report.repos.ProductRepository;
import centrikt.factory_monitoring.daily_report.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;
    private ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Autowired
    public void setProductMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public ProductDTO create(ProductDTO dto) {
        return productMapper.toDTO(productRepository.save(productMapper.toEntity(dto)));
    }

    @Override
    public ProductDTO get(Long id) {
        return productMapper.toDTO(productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id)));
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto) {
        Product existingPosition = productMapper.toEntity(dto);
        if (productRepository.findById(id).isPresent()){
            existingPosition.setId(id);
        } else throw new EntityNotFoundException("Product not found with id: " + id);
        return productMapper.toDTO(productRepository.save(existingPosition));
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductDTO> getAll() {
        return productRepository.findAll().stream().map(productMapper::toDTO).toList();
    }
}
