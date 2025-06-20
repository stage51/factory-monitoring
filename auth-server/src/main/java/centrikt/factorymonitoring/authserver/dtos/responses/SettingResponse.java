package centrikt.factorymonitoring.authserver.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@Schema(description = "Вывод обновления настроек")
@AllArgsConstructor
@NoArgsConstructor
public class SettingResponse {
    @Schema(description = "Часовой пояс формата UTC+**:**")
    private String timezone;
    @Schema(description = "Подписка на рассылку")
    private boolean subscribe;
    @Schema(description = "Список уведомлений об отчетах на почту: DAILY, FIVE_MINUTE, MODE")
    private List<String> reportNotifications;
    @Schema(description = "Ссылка на аватар")
    private String avatarUrl;
}
