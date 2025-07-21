package ru.centrikt.transportmonitoringservice.application.mappers.navigation;

import ru.centrikt.transportmonitoringservice.application.configs.DateTimeConfig;
import ru.centrikt.transportmonitoringservice.application.mappers.SensorMapper;
import ru.centrikt.transportmonitoringservice.application.mappers.mode.ModePositionMapper;
import ru.centrikt.transportmonitoringservice.domain.enums.Status;
import ru.centrikt.transportmonitoringservice.domain.exceptions.EntityMappingException;
import ru.centrikt.transportmonitoringservice.domain.models.Sensor;
import ru.centrikt.transportmonitoringservice.domain.models.navigation.NavigationData;
import ru.centrikt.transportmonitoringservice.domain.models.navigation.NavigationLevelGauge;
import ru.centrikt.transportmonitoringservice.domain.models.navigation.NavigationReport;
import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.navigation.NavigationReportRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.navigation.NavigationReportResponse;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class NavigationReportMapper {
    public static NavigationReportResponse toResponse(NavigationReport navigationReport) {
        if (navigationReport == null) {
            throw new EntityMappingException("Mode Report cannot be null");
        }
        NavigationReportResponse dto = NavigationReportResponse.builder()
                .id(navigationReport.getId())
                .levelGauges(navigationReport.getLevelGauges().stream().map(NavigationLevelGaugeMapper::toResponse).toList())
                .sensor(SensorMapper.toResponse(navigationReport.getSensor()))
                .createdAt(navigationReport.getCreatedAt())
                .updatedAt(navigationReport.getUpdatedAt())
                .originalFilename(navigationReport.getOriginalFilename())
                .build();
        return dto;
    }

    public static NavigationReport toCreateEntity(NavigationReportRequest navigationReportRequest) {
        if (navigationReportRequest == null) {
            throw new EntityMappingException("Navigation Report request cannot be null");
        }
        NavigationReport navigationReport = new NavigationReport();
        navigationReport.setSensor(SensorMapper.toEntity(navigationReportRequest.getSensor()));
        navigationReport.setLevelGauges(navigationReportRequest.getLevelGauges().stream().map(NavigationLevelGaugeMapper::toEntity).toList());
        navigationReport.setData(NavigationDataMapper.toEntity(navigationReportRequest.getNavigationData()));
        navigationReport.setCreatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        navigationReport.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        navigationReport.setStatus(Status.fromDescription(navigationReportRequest.getStatus()));
        return navigationReport;
    }

    public static NavigationReport toUpdateEntity(NavigationReportRequest navigationReportRequest, NavigationReport navigationReport) {
        if (navigationReportRequest == null) {
            throw new EntityMappingException("Navigation Report request cannot be null");
        }
        navigationReport.setSensor(SensorMapper.toEntity(navigationReportRequest.getSensor()));

        List<NavigationLevelGauge> newNavigationLevelGauges = navigationReportRequest.getLevelGauges().stream().map(NavigationLevelGaugeMapper::toEntity).toList();
        navigationReport.getLevelGauges().clear();
        navigationReport.getLevelGauges().addAll(newNavigationLevelGauges);

        navigationReport.setData(NavigationDataMapper.toEntity(navigationReportRequest.getNavigationData()));
        navigationReport.setCreatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        navigationReport.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        navigationReport.setStatus(Status.fromDescription(navigationReportRequest.getStatus()));
        return navigationReport;
    }

    public static NavigationReport toUpdateEntity(NavigationReportRequest navigationReportRequest,
                                                  NavigationReport navigationReport,
                                                  List<NavigationLevelGauge> navigationLevelGauges,
                                                  NavigationData navigationData,
                                                  Sensor sensor) {
        if (navigationReportRequest == null) {
            throw new EntityMappingException("Navigation Report request cannot be null");
        }
        navigationReport.setSensor(sensor);

        navigationReport.getLevelGauges().clear();
        navigationReport.getLevelGauges().addAll(navigationLevelGauges);

        navigationReport.setData(navigationData);
        navigationReport.setCreatedAt(navigationReport.getCreatedAt());
        navigationReport.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        navigationReport.setStatus(Status.fromDescription(navigationReportRequest.getStatus()));
        return navigationReport;
    }
}
