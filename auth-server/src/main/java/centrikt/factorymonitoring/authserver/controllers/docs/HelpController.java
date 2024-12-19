package centrikt.factorymonitoring.authserver.controllers.docs;

import centrikt.factorymonitoring.authserver.dtos.requests.HelpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
        name = "Контроллер заявок в сервис",
        description = "Позволяет работать с заявлениями в сервис"
)
public interface HelpController {
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Отправка сообщения",
            description = "Отправляет заявления на почтовый сервис"
    )
    @PostMapping
    ResponseEntity<Void> sendHelpMessage(@RequestBody HelpRequest helpRequest);
}
