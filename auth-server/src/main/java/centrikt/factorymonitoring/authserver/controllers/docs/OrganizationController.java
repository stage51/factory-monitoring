package centrikt.factorymonitoring.authserver.controllers.docs;

import centrikt.factorymonitoring.authserver.dtos.extra.PageRequestDTO;
import centrikt.factorymonitoring.authserver.dtos.requests.OrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.OrganizationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Контроллер организаций",
        description = "Позволяет работать с организациями"
)
public interface OrganizationController {

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Вывод организации",
            description = "Выводит организацию по заданному id"
    )
    @GetMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<OrganizationResponse> getOrganization(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Создание организации",
            description = "Создает организацию"
    )
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<OrganizationResponse> createOrganization(@RequestBody OrganizationRequest organizationRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Обновление организации",
            description = "Обновление организацию по заданному id и с требованием ввода всех полей"
    )
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<OrganizationResponse> updateOrganization(@PathVariable Long id, @RequestBody OrganizationRequest organizationRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Удаление организации",
            description = "Удаляет организацию по заданному id"
    )
    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> deleteOrganization(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр организаций",
            description = "Выводит организации постранично"
    )
    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    ResponseEntity<Page<OrganizationResponse>> getPage(
            @RequestBody PageRequestDTO pageRequestDTO
    );

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Создание организации",
            description = "Создает организацию для пользователя на основе его access токена"
    )
    @PostMapping("/profile")
    ResponseEntity<OrganizationResponse> createOrganization(@RequestHeader("Authorization") String authorizationHeader, @RequestBody AuthOrganizationRequest request);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Обновление организации",
            description = "Обновляет организацию пользователя на основе его access токена"
    )
    @PutMapping("/profile")
    ResponseEntity<OrganizationResponse> updateOrganization(@RequestHeader("Authorization") String authorizationHeader, @RequestBody AuthOrganizationRequest request);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Удаление организации",
            description = "Удаляет организацию пользователя на основе его access токена"
    )
    @DeleteMapping("/profile")
    ResponseEntity<OrganizationResponse> deleteOrganization(@RequestHeader("Authorization") String authorizationHeader);
}
