package centrikt.factorymonitoring.modereport.services.impl;

import centrikt.factorymonitoring.modereport.dtos.requests.ProductRequest;
import centrikt.factorymonitoring.modereport.dtos.responses.ProductResponse;
import centrikt.factorymonitoring.modereport.exceptions.EntityNotFoundException;
import centrikt.factorymonitoring.modereport.mappers.ProductMapper;
import centrikt.factorymonitoring.modereport.models.Product;
import centrikt.factorymonitoring.modereport.repos.ProductRepository;
import centrikt.factorymonitoring.modereport.services.ProductService;
import centrikt.factorymonitoring.modereport.utils.filter.FilterUtil;
import centrikt.factorymonitoring.modereport.utils.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {


    private ProductRepository productRepository;
    private EntityValidator entityValidator;
    private FilterUtil<Product> filterUtil;

    public ProductServiceImpl(ProductRepository productRepository, EntityValidator entityValidator, FilterUtil<Product> filterUtil) {
        this.productRepository = productRepository;
        this.entityValidator = entityValidator;
        this.filterUtil = filterUtil;
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Autowired
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
    }
    @Autowired
    public void setFilterUtil(FilterUtil<Product> filterUtil) {
        this.filterUtil = filterUtil;
    }
    @Override
    public ProductResponse create(ProductRequest dto) {
        entityValidator.validate(dto);
        return ProductMapper.toResponse(productRepository.save(ProductMapper.toEntity(dto)));
    }

    @Override
    public ProductResponse get(Long id) {
        return ProductMapper.toResponse(productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id)));
    }

    @Override
    public ProductResponse update(Long id, ProductRequest dto) {
        entityValidator.validate(dto);
        Product existingPosition = ProductMapper.toEntity(dto);
        if (productRepository.findById(id).isPresent()){
            existingPosition.setId(id);
        } else throw new EntityNotFoundException("Product not found with id: " + id);
        return ProductMapper.toResponse(productRepository.save(existingPosition));
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream().map(ProductMapper::toResponse).toList();
    }

    @Override
    public Page<ProductResponse> getPage(int size, int number, String sortBy, String sortDirection,
                                         Map<String, String> filters, Map<String, String> dateRanges) {
        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "id");
        Pageable pageable = PageRequest.of(number, size, sort);
        Specification<Product> specification = filterUtil.buildSpecification(filters, dateRanges);
        return productRepository.findAll(specification, pageable).map(ProductMapper::toResponse);
    }
}
