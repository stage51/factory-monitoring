package centrikt.factorymonitoring.authserver.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "Вывод обновления настроек")
public class SettingResponse {
    @Schema(description = "Часовой пояс формата UTC+**:**")
    private String timezone;
    @Schema(description = "Подписка на рассылку")
    private boolean subscribe;
    @Schema(description = "Список уведомлений об отчетах на почту: DAILY, FIVE_MINUTE, MODE")
    private List<String> reportNotifications;
}
