package ru.centrikt.transportmonitoringservice.presentation.dtos.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportMessage {
    private String taxpayerNumber;
    private String serialNumber;
    private String reportType;
    private String message;
}