package centrikt.factory_monitoring.daily_report.controllers.docs;

import centrikt.factory_monitoring.daily_report.dtos.extra.PageRequest;
import centrikt.factory_monitoring.daily_report.dtos.responses.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
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
            summary = "Просмотр продуктов",
            description = "Выводит продукты постранично"
    )
    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    ResponseEntity<Page<ProductResponse>> getPagePositions(
            @RequestBody PageRequest pageRequest
    );
}
