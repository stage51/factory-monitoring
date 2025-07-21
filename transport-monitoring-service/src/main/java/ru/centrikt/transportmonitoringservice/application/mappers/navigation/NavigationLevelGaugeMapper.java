package ru.centrikt.transportmonitoringservice.application.mappers.navigation;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.transportmonitoringservice.domain.models.navigation.NavigationLevelGauge;
import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.navigation.NavigationLevelGaugeRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.navigation.NavigationLevelGaugeResponse;

public class NavigationLevelGaugeMapper {
    public static NavigationLevelGaugeResponse toResponse(NavigationLevelGauge navigationLevelGauge) {
        if (navigationLevelGauge == null) {
            return null;
        }
        NavigationLevelGaugeResponse dto = NavigationLevelGaugeResponse.builder()
                .id(navigationLevelGauge.getId())
                .number(navigationLevelGauge.getNumber())
                .readings(navigationLevelGauge.getReadings())
                .temperature(navigationLevelGauge.getTemperature())
                .density(navigationLevelGauge.getDensity())
                .build();
        return dto;
    }

    public static NavigationLevelGauge toEntity(NavigationLevelGaugeRequest navigationLevelGaugeRequest) {
        if (navigationLevelGaugeRequest == null) {
            return null;
        }
        NavigationLevelGauge navigationLevelGauge = new NavigationLevelGauge();
        return setFields(navigationLevelGaugeRequest, navigationLevelGauge);
    }

    public static NavigationLevelGauge toEntity(NavigationLevelGaugeRequest navigationLevelGaugeRequest, NavigationLevelGauge navigationLevelGauge) {
        if (navigationLevelGaugeRequest == null) {
            return null;
        }
        return setFields(navigationLevelGaugeRequest, navigationLevelGauge);
    }

    @NotNull
    private static NavigationLevelGauge setFields(NavigationLevelGaugeRequest navigationLevelGaugeRequest, NavigationLevelGauge navigationLevelGauge) {
        navigationLevelGauge.setNumber(navigationLevelGaugeRequest.getNumber());
        navigationLevelGauge.setReadings(navigationLevelGaugeRequest.getReadings());
        navigationLevelGauge.setTemperature(navigationLevelGaugeRequest.getTemperature());
        navigationLevelGauge.setDensity(navigationLevelGaugeRequest.getDensity());
        return navigationLevelGauge;
    }
}
