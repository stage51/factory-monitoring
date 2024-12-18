package centrikt.factorymonitoring.authserver.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class SettingRequest {
    @NotNull
    private String timezone;
    @NotNull
    private boolean subscribe;
    @NotNull
    private List<String> reportNotifications;
}
