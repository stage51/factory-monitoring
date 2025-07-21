package ru.centrikt.transportmonitoringservice.application.services;


import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.daily.DailyReportRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.ReportStatusResponse;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.daily.DailyReportResponse;

import java.util.List;

public interface DailyReportService extends AbstractService<DailyReportRequest, DailyReportResponse> {
    DailyReportResponse create(DailyReportRequest dto);
    DailyReportResponse update(Long id, DailyReportRequest dto);
    void delete(Long id);
}
