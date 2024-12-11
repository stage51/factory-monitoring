package centrikt.factory_monitoring.five_minute_report.services;


import centrikt.factory_monitoring.five_minute_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.ReportStatusResponse;
import centrikt.factory_monitoring.five_minute_report.models.Position;

import java.util.List;

public interface PositionService extends CrudService<PositionRequest, PositionResponse>{
    List<PositionResponse> createAll(List<PositionRequest> positionRequests);
    List<ReportStatusResponse> getReportStatuses(String taxpayerNumber);
}
