package centrikt.factory_monitoring.five_minute_report.controllers;

import centrikt.factory_monitoring.five_minute_report.dtos.extra.PageRequestDTO;
import centrikt.factory_monitoring.five_minute_report.dtos.requests.ProductRequest;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.ProductResponse;
import centrikt.factory_monitoring.five_minute_report.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/five-minute-report/products")
@Slf4j
public class ProductController implements centrikt.factory_monitoring.five_minute_report.controllers.docs.ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
        log.info("ProductController initialized with ProductService: {}", productService.getClass().getName());
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
        log.debug("ProductService set to: {}", productService.getClass().getName());
    }

    @GetMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        log.info("Fetching product with id: {}", id);
        ProductResponse product = productService.get(id);
        log.debug("Fetched product: {}", product);
        return ResponseEntity.ok(product);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        log.info("Creating new product: {}", productRequest);
        ProductResponse createdProduct = productService.create(productRequest);
        log.debug("Created product: {}", createdProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        log.info("Updating product with id: {}", id);
        ProductResponse updatedProduct = productService.update(id, productRequest);
        log.debug("Updated product: {}", updatedProduct);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("Deleting product with id: {}", id);
        productService.delete(id);
        log.debug("Product with id: {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<ProductResponse>> getPagePositions(
            @RequestBody PageRequestDTO pageRequestDTO
    ) {
        log.info("Fetching page positions with filters: {}, dateRanges: {}", pageRequestDTO.getFilters(), pageRequestDTO.getDateRanges());
        Page<ProductResponse> products = productService.getPage(
                pageRequestDTO.getSize(),
                pageRequestDTO.getNumber(),
                pageRequestDTO.getSortBy(),
                pageRequestDTO.getSortDirection(),
                pageRequestDTO.getFilters(),
                pageRequestDTO.getDateRanges()
        );
        log.debug("Fetched products page: {}", products);
        return ResponseEntity.ok(products);
    }
}

