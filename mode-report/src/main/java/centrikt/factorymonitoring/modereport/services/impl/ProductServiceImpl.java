package centrikt.factorymonitoring.modereport.services.impl;

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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;
    private EntityValidator entityValidator;
    private FilterUtil<Product> filterUtil;

    public ProductServiceImpl(ProductRepository productRepository, EntityValidator entityValidator, FilterUtil<Product> filterUtil) {
        this.productRepository = productRepository;
        this.entityValidator = entityValidator;
        this.filterUtil = filterUtil;
        log.info("ProductServiceImpl initialized");
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
        log.debug("ProductRepository set");
    }

    @Autowired
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
        log.debug("EntityValidator set");
    }

    @Autowired
    public void setFilterUtil(FilterUtil<Product> filterUtil) {
        this.filterUtil = filterUtil;
        log.debug("FilterUtil set");
    }

    @Override
    public ProductResponse get(Long id) {
        log.trace("Entering get method with id: {}", id);
        ProductResponse response = ProductMapper.toResponse(productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", id);
                    return new EntityNotFoundException("Product not found with id: " + id);
                }));
        log.info("Fetched Product with id: {}", id);
        log.trace("Exiting get method with response: {}", response);
        return response;
    }

    @Override
    public List<ProductResponse> getAll() {
        log.trace("Entering getAll method");
        List<ProductResponse> responses = productRepository.findAll().stream()
                .map(ProductMapper::toResponse)
                .toList();
        log.info("Fetched all products");
        log.trace("Exiting getAll method with responses: {}", responses);
        return responses;
    }

    @Override
    public Page<ProductResponse> getPage(int size, int number, String sortBy, String sortDirection,
                                         Map<String, String> filters, Map<String, String> dateRanges) {
        log.trace("Entering getPage method with size: {}, number: {}, sortBy: {}, sortDirection: {}, filters: {}, dateRanges: {}",
                size, number, sortBy, sortDirection, filters, dateRanges);
        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "id");
        Pageable pageable = PageRequest.of(number, size, sort);
        Specification<Product> specification = filterUtil.buildSpecification(filters, dateRanges);
        Page<ProductResponse> page = productRepository.findAll(specification, pageable).map(ProductMapper::toResponse);
        log.info("Fetched paginated products with size: {}, number: {}", size, number);
        log.trace("Exiting getPage method with page: {}", page);
        return page;
    }
}

