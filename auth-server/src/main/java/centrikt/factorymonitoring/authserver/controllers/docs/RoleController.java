package centrikt.factorymonitoring.authserver.controllers.docs;

import centrikt.factorymonitoring.authserver.dtos.responses.RoleResponse;
import centrikt.factorymonitoring.authserver.models.enums.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Tag(
        name = "Контроллер ролей",
        description = "Позволяет работать с ролями"
)
public interface RoleController {
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр ролей",
            description = "Выводит информацию о ролях постранично"
    )
    @GetMapping()
    ResponseEntity<Page<RoleResponse>> getRoles();
}
