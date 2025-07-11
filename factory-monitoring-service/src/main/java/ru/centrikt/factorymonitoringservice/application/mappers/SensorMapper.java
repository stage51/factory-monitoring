package ru.centrikt.factorymonitoringservice.application.mappers;

import jakarta.validation.constraints.NotNull;
import ru.centrikt.factorymonitoringservice.domain.exceptions.EntityMappingException;
import ru.centrikt.factorymonitoringservice.domain.models.Sensor;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.SensorRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.SensorResponse;

public class SensorMapper {
    public static SensorResponse toResponse(Sensor sensor) {
        if (sensor == null) {
            throw new EntityMappingException("Sensor cannot be null");
        }
        SensorResponse dto = SensorResponse.builder().id(sensor.getId()).sensorNumber(sensor.getControllerNumber() + "_" + sensor.getLineNumber())
                .taxpayerNumber(sensor.getTaxpayerNumber()).build();
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
        sensor.setTaxpayerNumber(sensorRequest.getTaxpayerNumber());
        sensor.setControllerNumber(sensorRequest.getSensorNumber().split("_")[0]);
        sensor.setLineNumber(sensorRequest.getSensorNumber().split("_")[1]);
        return sensor;
    }
}
