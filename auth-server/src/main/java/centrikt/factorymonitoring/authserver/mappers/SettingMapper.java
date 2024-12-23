package centrikt.factorymonitoring.authserver.mappers;

import centrikt.factorymonitoring.authserver.dtos.requests.SettingRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.SettingResponse;
import centrikt.factorymonitoring.authserver.models.Setting;
import centrikt.factorymonitoring.authserver.models.enums.ReportNotification;

import java.util.stream.Collectors;

public class SettingMapper {
    public static SettingResponse toResponse(Setting setting) {
        return SettingResponse.builder().subscribe(setting.isSubscribe()).timezone(setting.getTimezone()).reportNotifications(
                setting.getReportNotifications().stream().map(ReportNotification::toString).collect(Collectors.toList())).
                avatarUrl(setting.getAvatarUrl()).build();
    }
    public static Setting toEntity(Setting setting, SettingRequest settingRequest) {
        setting.setSubscribe(settingRequest.isSubscribe());
        setting.setTimezone(settingRequest.getTimezone());
        setting.setReportNotifications(settingRequest.getReportNotifications().stream().map(ReportNotification::valueOf).collect(Collectors.toList()));
        setting.setAvatarUrl(settingRequest.getAvatarUrl());
        return setting;
    }
}
