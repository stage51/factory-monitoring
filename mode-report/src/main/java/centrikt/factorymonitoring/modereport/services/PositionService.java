package centrikt.factorymonitoring.modereport.services;


import centrikt.factorymonitoring.modereport.dtos.requests.PositionRequest;
import centrikt.factorymonitoring.modereport.dtos.responses.PositionResponse;

import java.util.List;

public interface PositionService extends ReadService<PositionRequest, PositionResponse> {
    PositionResponse create(PositionRequest dto);
    PositionResponse update(Long id, PositionRequest dto);
    void delete(Long id);
    List<PositionResponse> createAll(List<PositionRequest> positionRequests);
}
