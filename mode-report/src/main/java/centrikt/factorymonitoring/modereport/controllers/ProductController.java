package centrikt.factorymonitoring.modereport.controllers;

import centrikt.factorymonitoring.modereport.dtos.extra.PageRequest;
import centrikt.factorymonitoring.modereport.dtos.requests.ProductRequest;
import centrikt.factorymonitoring.modereport.dtos.responses.ProductResponse;
import centrikt.factorymonitoring.modereport.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/mode-report/products")
@Slf4j
public class ProductController implements centrikt.factorymonitoring.modereport.controllers.docs.ProductController {
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

    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<ProductResponse>> getPagePositions(
            @RequestBody PageRequest pageRequest
    ) {
        log.info("Fetching page positions with filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());
        Page<ProductResponse> products = productService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );
        log.debug("Fetched products page: {}", products);
        return ResponseEntity.ok(products);
    }
}
