package centrikt.factory_monitoring.daily_report.services;

import centrikt.factory_monitoring.daily_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.daily_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.daily_report.models.Position;

public interface PositionService extends CrudService<PositionRequest, PositionResponse>{

}
