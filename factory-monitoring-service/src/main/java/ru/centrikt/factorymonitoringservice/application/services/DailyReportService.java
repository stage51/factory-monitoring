package ru.centrikt.factorymonitoringservice.application.services;

import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.daily.DailyReportRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.ReportStatusResponse;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.daily.DailyReportResponse;

import java.util.List;

public interface DailyReportService extends AbstractService<DailyReportRequest, DailyReportResponse> {
    DailyReportResponse create(DailyReportRequest dto);
    DailyReportResponse update(Long id, DailyReportRequest dto);
    void delete(Long id);

    List<ReportStatusResponse> getReportStatuses(String taxpayerNumber);
}
