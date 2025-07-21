package ru.centrikt.transportmonitoringservice.application.services;

import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.mode.ModeReportRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.mode.ModeReportResponse;

public interface ModeReportService extends AbstractService<ModeReportRequest, ModeReportResponse> {
    ModeReportResponse create(ModeReportRequest dto);
    ModeReportResponse update(Long id, ModeReportRequest dto);
    void delete(Long id);
}
