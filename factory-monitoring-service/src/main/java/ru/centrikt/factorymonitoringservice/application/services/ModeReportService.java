package ru.centrikt.factorymonitoringservice.application.services;

import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.mode.ModeReportRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.mode.ModeReportResponse;

public interface ModeReportService extends AbstractService<ModeReportRequest, ModeReportResponse> {
    ModeReportResponse create(ModeReportRequest dto);
    ModeReportResponse update(Long id, ModeReportRequest dto);
    void delete(Long id);
}
