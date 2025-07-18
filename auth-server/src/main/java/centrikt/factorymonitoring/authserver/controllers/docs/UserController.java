package centrikt.factorymonitoring.authserver.controllers.docs;

import centrikt.factorymonitoring.authserver.dtos.extra.PageRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.SettingRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.admin.AdminUserRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.SettingResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Контроллер пользователей",
        description = "Позволяет работать с пользователями"
)
public interface UserController {
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр пользователя",
            description = "Выводит информацию о пользователе по заданному id"
    )
    @GetMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<UserResponse> getUser(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр неверифицированных пользователей",
            description = "Выводит информацию о пользователях с ролью ROLE_GUEST постранично"
    )
    @PostMapping(value = "/verification/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    ResponseEntity<Page<UserResponse>> getNotVerifiedUserPage(
            @RequestBody PageRequest pageRequest
    );

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Верифицировать пользователя",
            description = "Изменение роли пользователя с ROLE_GUEST на ROLE_USER по заданному id"
    )
    @GetMapping("/{id}/approve")
    ResponseEntity<Void> approveUser(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Отклонить верификацию пользователя",
            description = "Банит пользователя по заданному id"
    )
    @GetMapping("/{id}/disapprove")
    ResponseEntity<Void> disapproveUser(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Создание пользователя",
            description = "Создает пользователя с временным паролем"
    )
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<UserResponse> createUser(@RequestBody AdminUserRequest userRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Обновление пользователя",
            description = "Обновляет пользователя, изменяя его пароль на временный по заданному id"
    )
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody AdminUserRequest userRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Удаление пользователя",
            description = "Удаляет пользователя по заданному id"
    )
    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Вывод пользователей",
            description = "Выводит пользователей постранично"
    )
    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    ResponseEntity<Page<UserResponse>> getPage(
            @RequestBody PageRequest pageRequest
    );

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр профиля",
            description = "Возвращает профиль пользователя по access токену"
    )
    @GetMapping("/profile")
    ResponseEntity<UserResponse> profile(@RequestHeader("Authorization") String authorizationHeader);
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Обновление профиля",
            description = "Обновляет профиль пользователя по access токену"
    )
    @PutMapping("/profile")
    ResponseEntity<UserResponse> updateProfile(@RequestHeader("Authorization") String authorizationHeader, @RequestBody UserRequest userRequest);
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Обновление настроек",
            description = "Обновляет настройки пользователя по access токену"
    )
    @PutMapping("/profile/setting")
    ResponseEntity<SettingResponse> updateSetting(@RequestHeader("Authorization") String authorizationHeader, @RequestBody SettingRequest settingRequest);
}
