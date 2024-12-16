package centrikt.factorymonitoring.authserver.controllers.docs;

import centrikt.factorymonitoring.authserver.dtos.requests.LoginRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.RefreshTokenRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.UserRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.AccessRefreshTokenResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.AccessTokenResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.ApiTokenResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Контроллер аутентификации и авторизации",
        description = "Позволяет работать с JWT-сессией"
)
public interface AuthController {

    @Operation(
            summary = "Логин",
            description = "Вход в аккаунт, при валидных данных возвращает access и refresh токен"
    )
    @PostMapping("/login")
    ResponseEntity<AccessRefreshTokenResponse> login(@RequestBody LoginRequest loginRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Обновление access-токена",
            description = "Позволяет получить новый access-токен при валидном refresh токене"
    )
    @PostMapping("/refresh-token")
    ResponseEntity<AccessTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request);

    @Operation(
            summary = "Регистрация",
            description = "Создает нового пользователя с ролью ROLE_GUEST"
    )
    @PostMapping("/register")
    ResponseEntity<UserResponse> register(@RequestBody UserRequest userRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Выход из аккаунта",
            description = "Выход из аккаунта с удалением refresh-токена пользователя"
    )
    @PostMapping("/logout")
    ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request);

    @Operation(
            summary = "Проверка авторизации",
            description = "Проверяет, авторизован ли пользователь"
    )
    @GetMapping("/check")
    ResponseEntity<Void> checkAuth(@RequestHeader("Authorization") String authorizationHeader);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Создание API-токена",
            description = "Генерирует JWT-токен с необходимым временем жизни"
    )
    @GetMapping("/create-api-token")
    ResponseEntity<ApiTokenResponse> createApiToken(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("expiration") @Parameter(description = "Время жизни в long") Long expiration);
}
