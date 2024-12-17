package centrikt.factory_monitoring.config_server.controllers.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(
        name = "Контроллер конфигурации",
        description = "Позволяет работать с конфигурацией"
)
public interface ConfigController {
    @Operation(
            summary = "Просмотр конфигурации для клиенского приложения",
            description = "Выводит значения конфигурации по ключу, который является путем к нему в Consul для настройки клиентского приложения"
    )
    @GetMapping("/client")
    ResponseEntity<String> getClientConfigValue(@RequestParam("key") String key);
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр общей конфигурации",
            description = "Выводит значения конфигурации по ключу, который является путем к нему в Consul"
    )
    @GetMapping("")
    ResponseEntity<String> getConfigValue(@RequestParam("key") String key);
    @Operation(
            summary = "Изменение конфигурации",
            description = "Изменяет значения конфигурации по ключу, который является путем к нему в Consul"
    )
    @PutMapping("")
    ResponseEntity<String> updateConfigValue(@RequestParam("key") String key, @RequestBody String value);
}
