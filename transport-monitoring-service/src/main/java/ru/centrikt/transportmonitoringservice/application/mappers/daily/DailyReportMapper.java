package ru.centrikt.transportmonitoringservice.application.mappers.daily;


import ru.centrikt.transportmonitoringservice.application.configs.DateTimeConfig;
import ru.centrikt.transportmonitoringservice.application.mappers.SensorMapper;
import ru.centrikt.transportmonitoringservice.domain.enums.Status;
import ru.centrikt.transportmonitoringservice.domain.exceptions.EntityMappingException;
import ru.centrikt.transportmonitoringservice.domain.models.Sensor;
import ru.centrikt.transportmonitoringservice.domain.models.daily.DailyPosition;
import ru.centrikt.transportmonitoringservice.domain.models.daily.DailyReport;
import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.daily.DailyReportRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.daily.DailyReportResponse;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class DailyReportMapper {
    public static DailyReportResponse toResponse(DailyReport dailyReport) {
        if (dailyReport == null) {
            throw new EntityMappingException("Daily Report cannot be null");
        }
        DailyReportResponse dto = DailyReportResponse.builder().id(dailyReport.getId())
                .positions(dailyReport.getPositions().stream().map(DailyPositionMapper::toResponse).toList())
                .sensor(SensorMapper.toResponse(dailyReport.getSensor()))
                .createdAt(dailyReport.getCreatedAt())
                .updatedAt(dailyReport.getUpdatedAt())
                .status(dailyReport.getStatus().getDescription())
                .originalFilename(dailyReport.getOriginalFilename())
                .build();
        return dto;
    }

    public static DailyReport toCreateEntity(DailyReportRequest dailyReportRequest) {
        if (dailyReportRequest == null) {
            throw new EntityMappingException("Daily Report request cannot be null");
        }
        DailyReport dailyReport = new DailyReport();
        dailyReport.setSensor(SensorMapper.toEntity(dailyReportRequest.getSensor()));
        dailyReport.setPositions(dailyReportRequest.getPositions().stream().map(DailyPositionMapper::toEntity).toList());
        dailyReport.setCreatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        dailyReport.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        dailyReport.setStatus(Status.fromDescription(dailyReportRequest.getStatus()));
        return dailyReport;
    }

    public static DailyReport toUpdateEntity(DailyReportRequest dailyReportRequest, DailyReport dailyReport) {
        if (dailyReportRequest == null) {
            throw new EntityMappingException("Daily Report request cannot be null");
        }
        dailyReport.setSensor(SensorMapper.toEntity(dailyReportRequest.getSensor()));

        List<DailyPosition> newPositions = dailyReportRequest.getPositions()
                .stream()
                .map(DailyPositionMapper::toEntity)
                .toList();

        dailyReport.getPositions().clear();
        dailyReport.getPositions().addAll(newPositions);

        dailyReport.setCreatedAt(dailyReport.getCreatedAt());
        dailyReport.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        dailyReport.setStatus(Status.fromDescription(dailyReportRequest.getStatus()));
        return dailyReport;
    }

    public static DailyReport toUpdateEntity(DailyReportRequest dailyReportRequest, DailyReport dailyReport, List<DailyPosition> positions, Sensor sensor) {
        if (dailyReportRequest == null) {
            throw new EntityMappingException("Daily Report request cannot be null");
        }
        dailyReport.setSensor(sensor);
        dailyReport.setPositions(positions);
        dailyReport.setCreatedAt(dailyReport.getCreatedAt());
        dailyReport.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        dailyReport.setStatus(Status.fromDescription(dailyReportRequest.getStatus()));
        return dailyReport;
    }
}
