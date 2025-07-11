package ru.centrikt.factorymonitoringservice.presentation.dtos.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportMessage {
    private String taxpayerNumber;
    private String sensorNumber;
    private String reportType;
    private String message;
}