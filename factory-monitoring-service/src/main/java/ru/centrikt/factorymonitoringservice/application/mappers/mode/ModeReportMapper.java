package ru.centrikt.factorymonitoringservice.application.mappers.mode;

import ru.centrikt.factorymonitoringservice.application.configs.DateTimeConfig;
import ru.centrikt.factorymonitoringservice.application.mappers.SensorMapper;
import ru.centrikt.factorymonitoringservice.domain.exceptions.EntityMappingException;
import ru.centrikt.factorymonitoringservice.domain.models.Sensor;
import ru.centrikt.factorymonitoringservice.domain.models.mode.ModeReport;
import ru.centrikt.factorymonitoringservice.domain.models.mode.ModePosition;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.mode.ModeReportRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.mode.ModeReportResponse;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ModeReportMapper {
    public static ModeReportResponse toResponse(ModeReport modeReport) {
        if (modeReport == null) {
            throw new EntityMappingException("Mode Report cannot be null");
        }
        ModeReportResponse dto = ModeReportResponse.builder().id(modeReport.getId())
                .position(ModePositionMapper.toResponse(modeReport.getPosition()))
                .sensor(SensorMapper.toResponse(modeReport.getSensor()))
                .createdAt(modeReport.getCreatedAt())
                .updatedAt(modeReport.getUpdatedAt())
                .originalFilename(modeReport.getOriginalFilename())
                .build();
        return dto;
    }

    public static ModeReport toCreateEntity(ModeReportRequest modeReportRequest) {
        if (modeReportRequest == null) {
            throw new EntityMappingException("Mode Report request cannot be null");
        }
        ModeReport modeReport = new ModeReport();
        modeReport.setSensor(SensorMapper.toEntity(modeReportRequest.getSensor()));
        modeReport.setPosition(ModePositionMapper.toEntity(modeReportRequest.getPosition()));
        modeReport.setCreatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        modeReport.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        return modeReport;
    }

    public static ModeReport toUpdateEntity(ModeReportRequest modeReportRequest, ModeReport modeReport) {
        if (modeReportRequest == null) {
            throw new EntityMappingException("Mode Report request cannot be null");
        }
        modeReport.setSensor(SensorMapper.toEntity(modeReportRequest.getSensor()));
        modeReport.setPosition(ModePositionMapper.toEntity(modeReportRequest.getPosition()));
        modeReport.setCreatedAt(modeReport.getCreatedAt());
        modeReport.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        return modeReport;
    }

    public static ModeReport toUpdateEntity(ModeReportRequest modeReportRequest, ModeReport modeReport, ModePosition position, Sensor sensor) {
        if (modeReportRequest == null) {
            throw new EntityMappingException("Mode Report request cannot be null");
        }
        modeReport.setSensor(sensor);
        modeReport.setPosition(position);
        modeReport.setCreatedAt(modeReport.getCreatedAt());
        modeReport.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        return modeReport;
    }
}
