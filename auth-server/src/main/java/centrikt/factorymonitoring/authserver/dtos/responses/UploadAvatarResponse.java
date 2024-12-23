package centrikt.factorymonitoring.authserver.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Вывод URL аватара пользователя")
public class UploadAvatarResponse {
    private String avatarUrl;
}
