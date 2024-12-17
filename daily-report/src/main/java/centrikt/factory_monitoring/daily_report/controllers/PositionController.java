package centrikt.factory_monitoring.daily_report.controllers;

import centrikt.factory_monitoring.daily_report.dtos.extra.PageRequestDTO;
import centrikt.factory_monitoring.daily_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.daily_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.daily_report.dtos.responses.ReportStatusResponse;
import centrikt.factory_monitoring.daily_report.services.PositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/v1/daily-report/positions")
@Slf4j
public class PositionController implements centrikt.factory_monitoring.daily_report.controllers.docs.PositionController {
    private PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }
    @Autowired
    public void setPositionService(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PositionResponse> getPosition(@PathVariable Long id) {
        log.info("Fetching position with id: {}", id);
        PositionResponse position = positionService.get(id);
        return ResponseEntity.ok(position);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PositionResponse> createPosition(@RequestBody PositionRequest positionRequest) {
        log.info("Creating new position: {}", positionRequest);
        PositionResponse createdPosition = positionService.create(positionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPosition);
    }

    @PostMapping(value = "/list", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<PositionResponse>> createPositions(@RequestBody List<PositionRequest> positionRequest) {
        log.info("Creating new positions: {}", positionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(positionService.createAll(positionRequest));
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PositionResponse> updatePosition(@PathVariable Long id, @RequestBody PositionRequest positionRequest) {
        log.info("Updating position with id: {}", id);
        PositionResponse updatedPosition = positionService.update(id, positionRequest);
        return ResponseEntity.ok(updatedPosition);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        log.info("Deleting position with id: {}", id);
        positionService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<Page<PositionResponse>> getPagePositions(
            @RequestBody PageRequestDTO pageRequestDTO
    ) {
        log.info("Fetching page positions with filters: {}, dateRanges: {}", pageRequestDTO.getFilters(), pageRequestDTO.getDateRanges());
        Page<PositionResponse> positions = positionService.getPage(
                pageRequestDTO.getSize(),
                pageRequestDTO.getNumber(),
                pageRequestDTO.getSortBy(),
                pageRequestDTO.getSortDirection(),
                pageRequestDTO.getFilters(),
                pageRequestDTO.getDateRanges()
        );
        return ResponseEntity.ok(positions);
    }

    @PostMapping(value = "/fetch/{taxpayerNumber}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<Page<PositionResponse>> getPagePositions(
            @RequestBody PageRequestDTO pageRequestDTO, @PathVariable String taxpayerNumber
    ) {
        Map<String, String> filters = pageRequestDTO.getFilters();
        filters.put("taxpayerNumber", taxpayerNumber);
        pageRequestDTO.setFilters(filters);
        log.info("Fetching page positions with filters: {}, dateRanges: {}", pageRequestDTO.getFilters(), pageRequestDTO.getDateRanges());
        Page<PositionResponse> positions = positionService.getPage(
                pageRequestDTO.getSize(),
                pageRequestDTO.getNumber(),
                pageRequestDTO.getSortBy(),
                pageRequestDTO.getSortDirection(),
                pageRequestDTO.getFilters(),
                pageRequestDTO.getDateRanges()
        );
        return ResponseEntity.ok(positions);
    }
    @GetMapping(value = "/check")
    public ResponseEntity<List<ReportStatusResponse>> checkLines(@RequestParam String taxpayerNumber) {
        return ResponseEntity.ok(positionService.getReportStatuses(taxpayerNumber));
    }
}
