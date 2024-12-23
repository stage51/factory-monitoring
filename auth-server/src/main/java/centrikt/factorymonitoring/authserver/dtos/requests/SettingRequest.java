package centrikt.factorymonitoring.authserver.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.util.List;

@Data
@Schema(description = "Запрос на обновление настроек")
public class SettingRequest {
    @Schema(description = "Часовой пояс формата UTC+**:**")
    @NotNull
    private String timezone;
    @Schema(description = "Подписка на рассылку")
    @NotNull
    private boolean subscribe;
    @Schema(description = "Список уведомлений об отчетах на почту: DAILY, FIVE_MINUTE, MODE")
    @NotNull
    private List<String> reportNotifications;
    @Schema(description = "Ссылка на аватар")
    private String avatarUrl;
}
