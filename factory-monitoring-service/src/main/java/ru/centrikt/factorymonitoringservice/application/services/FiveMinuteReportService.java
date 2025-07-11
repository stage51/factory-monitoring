package ru.centrikt.factorymonitoringservice.application.services;

import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.fiveminute.FiveMinuteReportRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.ReportStatusResponse;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.fiveminute.FiveMinuteReportResponse;

import java.util.List;

public interface FiveMinuteReportService extends AbstractService<FiveMinuteReportRequest, FiveMinuteReportResponse> {
    FiveMinuteReportResponse create(FiveMinuteReportRequest dto);
    FiveMinuteReportResponse update(Long id, FiveMinuteReportRequest dto);
    void delete(Long id);
    List<ReportStatusResponse> getReportStatuses(String taxpayerNumber);
}
