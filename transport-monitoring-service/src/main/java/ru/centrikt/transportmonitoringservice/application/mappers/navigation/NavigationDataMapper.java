package ru.centrikt.transportmonitoringservice.application.mappers.navigation;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.transportmonitoringservice.domain.models.navigation.NavigationData;
import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.navigation.NavigationDataRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.navigation.NavigationDataResponse;

public class NavigationDataMapper {
    public static NavigationDataResponse toResponse(NavigationData navigationData) {
        if (navigationData == null) {
            return null;
        }
        NavigationDataResponse dto = NavigationDataResponse.builder()
                .id(navigationData.getId())
                .navigationDate(navigationData.getNavigationDate())
                .latitude(navigationData.getLatitude())
                .longitude(navigationData.getLongitude())
                .countSatellite(navigationData.getCountSatellite())
                .accuracy(navigationData.getAccuracy())
                .course(navigationData.getCourse())
                .speed(navigationData.getSpeed())
                .build();
        return dto;
    }

    public static NavigationData toEntity(NavigationDataRequest navigationDataRequest) {
        if (navigationDataRequest == null) {
            return null;
        }
        NavigationData navigationData = new NavigationData();
        return setFields(navigationDataRequest, navigationData);
    }

    public static NavigationData toEntity(NavigationDataRequest navigationDataRequest, NavigationData navigationData) {
        if (navigationDataRequest == null) {
            return null;
        }
        return setFields(navigationDataRequest, navigationData);
    }

    @NotNull
    private static NavigationData setFields(NavigationDataRequest navigationDataRequest, NavigationData navigationData) {
        navigationData.setNavigationDate(navigationDataRequest.getNavigationDate());
        navigationData.setLatitude(navigationDataRequest.getLatitude());
        navigationData.setLongitude(navigationDataRequest.getLongitude());
        navigationData.setCountSatellite(navigationDataRequest.getCountSatellite());
        navigationData.setAccuracy(navigationDataRequest.getAccuracy());
        navigationData.setCourse(navigationDataRequest.getCourse());
        navigationData.setSpeed(navigationDataRequest.getSpeed());
        return navigationData;
    }
}
