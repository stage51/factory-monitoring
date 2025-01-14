package centrikt.factorymonitoring.authserver.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Schema(description = "Вывод URL аватара пользователя")
@AllArgsConstructor
@NoArgsConstructor
public class UploadAvatarResponse {
    private String avatarUrl;
}
