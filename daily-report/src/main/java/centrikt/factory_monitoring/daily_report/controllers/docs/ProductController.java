package centrikt.factory_monitoring.daily_report.controllers.docs;

import centrikt.factory_monitoring.daily_report.dtos.extra.PageRequestDTO;
import centrikt.factory_monitoring.daily_report.dtos.requests.ProductRequest;
import centrikt.factory_monitoring.daily_report.dtos.responses.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Контроллер продуктов",
        description = "Позволяет работать с продуктами"
)
public interface ProductController {

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр продукта",
            description = "Выводит продукт по заданному id"
    )
    @GetMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<ProductResponse> getProduct(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Создание продукта",
            description = "Создает продукт"
    )
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Обновление продукта",
            description = "Обновляет продукт по заданному id"
    )
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Удаление продукта",
            description = "Удаляет продукт по заданному id"
    )
    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> deleteProduct(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр продуктов",
            description = "Выводит продукты постранично"
    )
    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    ResponseEntity<Page<ProductResponse>> getPagePositions(
            @RequestBody PageRequestDTO pageRequestDTO
    );
}
