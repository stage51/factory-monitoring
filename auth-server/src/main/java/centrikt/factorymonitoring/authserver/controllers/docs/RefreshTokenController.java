package centrikt.factorymonitoring.authserver.controllers.docs;

import centrikt.factorymonitoring.authserver.dtos.extra.PageRequestDTO;
import centrikt.factorymonitoring.authserver.dtos.responses.RefreshTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
        name = "Контроллер refresh токенов",
        description = "Позволяет работать с refresh токенами"
)
public interface RefreshTokenController {
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр refresh-токенов",
            description = "Выводит выданные токены пользователям с датой выдачи и датой конца жизни постранично"
    )
    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    ResponseEntity<Page<RefreshTokenResponse>> getPage(
            @RequestBody PageRequestDTO pageRequestDTO
    );
}
