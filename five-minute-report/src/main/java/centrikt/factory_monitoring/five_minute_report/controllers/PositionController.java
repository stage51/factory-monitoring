package centrikt.factory_monitoring.five_minute_report.controllers;

import centrikt.factory_monitoring.five_minute_report.dtos.extra.PageRequest;
import centrikt.factory_monitoring.five_minute_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.ReportStatusResponse;
import centrikt.factory_monitoring.five_minute_report.services.PositionService;
import centrikt.factory_monitoring.five_minute_report.utils.Message;
import centrikt.factory_monitoring.five_minute_report.utils.ftp.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/v1/five-minute-report/positions")
@Slf4j
public class PositionController implements centrikt.factory_monitoring.five_minute_report.controllers.docs.PositionController {
    private PositionService positionService;
    private FTPUtil ftpUtil;

    public PositionController(PositionService positionService, FTPUtil ftpUtil) {
        this.positionService = positionService;
        this.ftpUtil = ftpUtil;
        log.info("PositionController initialized with PositionService: {} and FTPUtil: {}", positionService.getClass().getName(), ftpUtil.getClass().getName());
    }

    @Autowired
    public void setPositionService(PositionService positionService) {
        this.positionService = positionService;
        log.debug("PositionService set to: {}", positionService.getClass().getName());
    }

    @Autowired
    public void setFtpUtil(FTPUtil ftpUtil) {
        this.ftpUtil = ftpUtil;
        log.debug("FTPUtil set to: {}", ftpUtil.getClass().getName());
    }

    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PositionResponse> getPosition(@PathVariable Long id) {
        log.info("Fetching position with id: {}", id);
        PositionResponse position = positionService.get(id);
        log.debug("Fetched position: {}", position);
        return ResponseEntity.ok(position);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PositionResponse> createPosition(@RequestBody PositionRequest positionRequest) {
        log.info("Creating new position: {}", positionRequest);
        PositionResponse createdPosition = positionService.create(positionRequest);
        log.debug("Created position: {}", createdPosition);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPosition);
    }

    @PostMapping(value = "/list", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<PositionResponse>> createPositions(@RequestBody List<PositionRequest> positionRequest) {
        log.info("Creating new positions: {}", positionRequest);
        List<PositionResponse> createdPositions = positionService.createAll(positionRequest);
        log.debug("Created positions: {}", createdPositions);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPositions);
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadReport(@RequestBody MultipartFile file) {
        log.info("Uploading new file: {}", file.getOriginalFilename());
        try {
            String localFilePath = ftpUtil.saveFileLocally(file);
            boolean success = ftpUtil.saveFileToFTP(localFilePath, file.getOriginalFilename());
            if (success) {
                log.debug("File uploaded successfully: {}", file.getOriginalFilename());
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                log.error("Failed to upload file: {}", file.getOriginalFilename());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Message("error", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to upload file"));
            }
        } catch (Exception e) {
            log.error("Error occurred while uploading file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Message("error", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error occurred while uploading file"));
        }
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PositionResponse> updatePosition(@PathVariable Long id, @RequestBody PositionRequest positionRequest) {
        log.info("Updating position with id: {}", id);
        PositionResponse updatedPosition = positionService.update(id, positionRequest);
        log.debug("Updated position: {}", updatedPosition);
        return ResponseEntity.ok(updatedPosition);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        log.info("Deleting position with id: {}", id);
        positionService.delete(id);
        log.debug("Position with id: {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<PositionResponse>> getPagePositions(@RequestBody PageRequest pageRequest) {
        log.info("Fetching page positions with filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());
        Page<PositionResponse> positions = positionService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );
        log.debug("Fetched positions page: {}", positions);
        return ResponseEntity.ok(positions);
    }

    @PostMapping(value = "/fetch/{taxpayerNumber}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<PositionResponse>> getPagePositions(@RequestBody PageRequest pageRequest, @PathVariable String taxpayerNumber) {
        Map<String, String> filters = pageRequest.getFilters();
        filters.put("taxpayerNumber", taxpayerNumber);
        pageRequest.setFilters(filters);
        log.info("Fetching page positions with filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());
        Page<PositionResponse> positions = positionService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );
        log.debug("Fetched positions page: {}", positions);
        return ResponseEntity.ok(positions);
    }
    @GetMapping(value = "/check")
    public ResponseEntity<List<ReportStatusResponse>> checkLines(@RequestParam String taxpayerNumber) {
        log.debug("Check report statuses for taxpayerNumber: {}", taxpayerNumber);
        return ResponseEntity.ok(positionService.getReportStatuses(taxpayerNumber));
    }
}
