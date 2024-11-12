package centrikt.factory_monitoring.five_minute_report.services;


import centrikt.factory_monitoring.five_minute_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.five_minute_report.models.Position;

public interface PositionService extends CrudService<PositionRequest, PositionResponse>{

}
