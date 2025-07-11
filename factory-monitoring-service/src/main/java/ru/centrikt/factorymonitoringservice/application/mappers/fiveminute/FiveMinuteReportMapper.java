package ru.centrikt.factorymonitoringservice.application.mappers.fiveminute;

import ru.centrikt.factorymonitoringservice.application.configs.DateTimeConfig;
import ru.centrikt.factorymonitoringservice.application.mappers.SensorMapper;
import ru.centrikt.factorymonitoringservice.domain.enums.Status;
import ru.centrikt.factorymonitoringservice.domain.exceptions.EntityMappingException;
import ru.centrikt.factorymonitoringservice.domain.models.Sensor;
import ru.centrikt.factorymonitoringservice.domain.models.fiveminute.FiveMinuteReport;
import ru.centrikt.factorymonitoringservice.domain.models.fiveminute.FiveMinutePosition;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.fiveminute.FiveMinuteReportRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.fiveminute.FiveMinuteReportResponse;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class FiveMinuteReportMapper {
    public static FiveMinuteReportResponse toResponse(FiveMinuteReport fiveMinuteReport) {
        if (fiveMinuteReport == null) {
            throw new EntityMappingException("Five Minute Report cannot be null");
        }
        FiveMinuteReportResponse dto = FiveMinuteReportResponse.builder().id(fiveMinuteReport.getId())
                .position(FiveMinutePositionMapper.toResponse(fiveMinuteReport.getPosition()))
                .sensor(SensorMapper.toResponse(fiveMinuteReport.getSensor()))
                .createdAt(fiveMinuteReport.getCreatedAt())
                .updatedAt(fiveMinuteReport.getUpdatedAt())
                .status(fiveMinuteReport.getStatus().getDescription())
                .originalFilename(fiveMinuteReport.getOriginalFilename())
                .build();
        return dto;
    }

    public static FiveMinuteReport toCreateEntity(FiveMinuteReportRequest fiveMinuteReportRequest) {
        if (fiveMinuteReportRequest == null) {
            throw new EntityMappingException("Five Minute Report request cannot be null");
        }
        FiveMinuteReport fiveMinuteReport = new FiveMinuteReport();
        fiveMinuteReport.setSensor(SensorMapper.toEntity(fiveMinuteReportRequest.getSensor()));
        fiveMinuteReport.setPosition(FiveMinutePositionMapper.toEntity(fiveMinuteReportRequest.getPosition()));
        fiveMinuteReport.setCreatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        fiveMinuteReport.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        fiveMinuteReport.setStatus(Status.fromDescription(fiveMinuteReportRequest.getStatus()));
        return fiveMinuteReport;
    }

    public static FiveMinuteReport toUpdateEntity(FiveMinuteReportRequest fiveMinuteReportRequest, FiveMinuteReport fiveMinuteReport) {
        if (fiveMinuteReportRequest == null) {
            throw new EntityMappingException("Five Minute Report request cannot be null");
        }
        fiveMinuteReport.setSensor(SensorMapper.toEntity(fiveMinuteReportRequest.getSensor()));
        fiveMinuteReport.setPosition(FiveMinutePositionMapper.toEntity(fiveMinuteReportRequest.getPosition()));
        fiveMinuteReport.setCreatedAt(fiveMinuteReport.getCreatedAt());
        fiveMinuteReport.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        fiveMinuteReport.setStatus(Status.fromDescription(fiveMinuteReportRequest.getStatus()));
        return fiveMinuteReport;
    }

    public static FiveMinuteReport toUpdateEntity(FiveMinuteReportRequest fiveMinuteReportRequest, FiveMinuteReport fiveMinuteReport, FiveMinutePosition position, Sensor sensor) {
        if (fiveMinuteReportRequest == null) {
            throw new EntityMappingException("Five Minute Report request cannot be null");
        }
        fiveMinuteReport.setSensor(sensor);
        fiveMinuteReport.setPosition(position);
        fiveMinuteReport.setCreatedAt(fiveMinuteReport.getCreatedAt());
        fiveMinuteReport.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        fiveMinuteReport.setStatus(Status.fromDescription(fiveMinuteReportRequest.getStatus()));
        return fiveMinuteReport;
    }
}
