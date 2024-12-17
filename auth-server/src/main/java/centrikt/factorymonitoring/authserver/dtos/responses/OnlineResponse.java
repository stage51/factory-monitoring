package centrikt.factorymonitoring.authserver.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
@Schema(description = "Форма вывода онлайна пользователя")
public class OnlineResponse {
    private Long id;
    private String email;
    private String ipAddress;
    private ZonedDateTime activeAt;
}
