package centrikt.factorymonitoring.modereport.services;

import centrikt.factorymonitoring.modereport.dtos.requests.ProductRequest;
import centrikt.factorymonitoring.modereport.dtos.responses.ProductResponse;
import centrikt.factorymonitoring.modereport.enums.UnitType;
import centrikt.factorymonitoring.modereport.exceptions.EntityNotFoundException;
import centrikt.factorymonitoring.modereport.models.Product;
import centrikt.factorymonitoring.modereport.repos.ProductRepository;
import centrikt.factorymonitoring.modereport.services.impl.ProductServiceImpl;
import centrikt.factorymonitoring.modereport.utils.filter.FilterUtil;
import centrikt.factorymonitoring.modereport.utils.validator.EntityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EntityValidator entityValidator;

    @Mock
    private FilterUtil<Product> filterUtil;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest productRequest;
    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        productRequest = new ProductRequest();
        productRequest.setFullName("Product Name");
        productRequest.setAlcCode("12345");
        productRequest.setProductVCode("54321");
        productRequest.setUnitType(UnitType.PACKED.getUnitType());

        product = new Product();
        product.setId(1L);
        product.setFullName("Product Name");
        product.setAlcCode("12345");
        product.setProductVCode("54321");
        product.setUnitType(UnitType.PACKED);

        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setFullName("Product Name");
        productResponse.setAlcCode("12345");
        productResponse.setProductVCode("54321");
        productResponse.setUnitType(UnitType.PACKED.getUnitType());
    }

    @Test
    void get_ShouldReturnProductResponse_WhenProductExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.get(1L);

        assertNotNull(response);
        assertEquals(productResponse.getId(), response.getId());
    }

    @Test
    void get_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.get(1L));
    }


    @Test
    void getAll_ShouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));

        List<ProductResponse> responses = productService.getAll();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(productResponse.getId(), responses.get(0).getId());
    }

    @Test
    void getPage_ShouldReturnPagedProducts() {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
        when(filterUtil.buildSpecification(any(Map.class), any(Map.class))).thenReturn((Specification<Product>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("alcCode"), product.getAlcCode()));
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);

        Page<ProductResponse> responses = productService.getPage(10, 0, "id", "ASC", new HashMap<>(), new HashMap<>());

        assertNotNull(responses);
        assertEquals(1, responses.getContent().size());
        assertEquals(productResponse.getId(), responses.getContent().get(0).getId());
    }
}

