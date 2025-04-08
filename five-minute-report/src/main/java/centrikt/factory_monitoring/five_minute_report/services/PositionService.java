package centrikt.factory_monitoring.five_minute_report.services;


import centrikt.factory_monitoring.five_minute_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.ReportStatusResponse;

import java.util.List;

public interface PositionService extends ReadService<PositionRequest, PositionResponse> {
    PositionResponse create(PositionRequest dto);
    PositionResponse update(Long id, PositionRequest dto);
    void delete(Long id);
    List<PositionResponse> createAll(List<PositionRequest> positionRequests);
    List<ReportStatusResponse> getReportStatuses(String taxpayerNumber);
}
