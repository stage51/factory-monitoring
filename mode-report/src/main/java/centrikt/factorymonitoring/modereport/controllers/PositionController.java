package centrikt.factorymonitoring.modereport.controllers;

import centrikt.factorymonitoring.modereport.dtos.extra.PageRequestDTO;
import centrikt.factorymonitoring.modereport.dtos.requests.PositionRequest;
import centrikt.factorymonitoring.modereport.dtos.responses.PositionResponse;
import centrikt.factorymonitoring.modereport.services.PositionService;
import centrikt.factorymonitoring.modereport.utils.Message;
import centrikt.factorymonitoring.modereport.utils.ftp.FTPUtil;
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
@RequestMapping(value = "api/v1/mode-report/positions")
@Slf4j
public class PositionController implements centrikt.factorymonitoring.modereport.controllers.docs.PositionController {
    private PositionService positionService;
    private FTPUtil ftpUtil;

    public PositionController(PositionService positionService, FTPUtil ftpUtil) {
        this.positionService = positionService;
        this.ftpUtil = ftpUtil;
    }
    @Autowired
    public void setPositionService(PositionService positionService) {
        this.positionService = positionService;
    }

    @Autowired
    public void setFtpUtil(FTPUtil ftpUtil) {
        this.ftpUtil = ftpUtil;
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

    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadReport(@RequestBody MultipartFile file) {
        log.info("Uploading new file: {}", file.getOriginalFilename());
        String localFilePath = ftpUtil.saveFileLocally(file);
        boolean success = ftpUtil.saveFileToFTP(localFilePath, file.getOriginalFilename());
        if (success) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Message("error", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to upload file"));
        }
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
}
