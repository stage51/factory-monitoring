package centrikt.factorymonitoring.modereport.services;


import centrikt.factorymonitoring.modereport.dtos.requests.PositionRequest;
import centrikt.factorymonitoring.modereport.dtos.responses.PositionResponse;

import java.util.List;

public interface PositionService extends CrudService<PositionRequest, PositionResponse> {
    List<PositionResponse> createAll(List<PositionRequest> positionRequests);
}
