package ru.centrikt.transportmonitoringservice.application.mappers;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.transportmonitoringservice.domain.exceptions.EntityMappingException;
import ru.centrikt.transportmonitoringservice.domain.models.Sensor;
import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.SensorRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.SensorResponse;

public class SensorMapper {
    public static SensorResponse toResponse(Sensor sensor) {
        if (sensor == null) {
            throw new EntityMappingException("Sensor cannot be null");
        }
        SensorResponse dto = SensorResponse.builder()
                .id(sensor.getId())
                .organizationName(sensor.getOrganizationName())
                .govNumber(sensor.getGovNumber())
                .taxpayerNumber(sensor.getTaxpayerNumber())
                .guid(sensor.getGuid())
                .build();
        return dto;
    }

    public static Sensor toEntity(SensorRequest sensorRequest) {
        if (sensorRequest == null) {
            throw new EntityMappingException("Sensor request cannot be null");
        }
        Sensor sensor = new Sensor();
        return setFields(sensorRequest, sensor);
    }

    public static Sensor toEntity(SensorRequest sensorRequest, Sensor sensor) {
        if (sensor == null) {
            throw new EntityMappingException("Sensor cannot be null");
        }
        if (sensorRequest == null) {
            throw new EntityMappingException("Sensor request cannot be null");
        }
        return setFields(sensorRequest, sensor);
    }

    @NotNull
    private static Sensor setFields(SensorRequest sensorRequest, Sensor sensor) {
        sensor.setOrganizationName(sensorRequest.getOrganizationName());
        sensor.setGovNumber(sensorRequest.getGovNumber());
        sensor.setTaxpayerNumber(sensorRequest.getTaxpayerNumber());
        sensor.setGuid(sensorRequest.getGuid());
        return sensor;
    }
}
