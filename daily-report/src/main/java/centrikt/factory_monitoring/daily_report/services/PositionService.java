package centrikt.factory_monitoring.daily_report.services;

import centrikt.factory_monitoring.daily_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.daily_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.daily_report.dtos.responses.ReportStatusResponse;
import centrikt.factory_monitoring.daily_report.models.Position;

import java.util.List;

public interface PositionService extends CrudService<PositionRequest, PositionResponse>{
    List<PositionResponse> createAll(List<PositionRequest> positionRequests);
    List<ReportStatusResponse> getReportStatuses(String taxpayerNumber);
}
