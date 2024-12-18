package centrikt.factorymonitoring.authserver.dtos.responses;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class SettingResponse {
    private String timezone;
    private boolean subscribe;
    private List<String> reportNotifications;
}
