package ru.centrikt.transportmonitoringservice.presentation.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.centrikt.transportmonitoringservice.application.services.ModeReportService;
import ru.centrikt.transportmonitoringservice.application.utils.Message;
import ru.centrikt.transportmonitoringservice.presentation.controllers.docs.AbstractModeReportController;
import ru.centrikt.transportmonitoringservice.presentation.dtos.extra.PageRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.mode.ModeReportRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.mode.ModeReportResponse;

import java.io.File;
import java.util.Map;

@RestController
@RequestMapping(value = "api/v1/transport-monitoring/mode-report")
@Slf4j
public class ModeReportController implements AbstractModeReportController {
    
    private ModeReportService modeReportService;

    public ModeReportController(ModeReportService modeReportService) {
        this.modeReportService = modeReportService;
        log.info("ModeReportController initialized with ModeReportService: {}", modeReportService.getClass().getName());
    }

    @Autowired
    public void setModeReportService(ModeReportService modeReportService) {
        this.modeReportService = modeReportService;
        log.debug("ModeReportService set to: {}", modeReportService.getClass().getName());
    }

    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ModeReportResponse> getModeReport(@PathVariable Long id) {
        log.info("Fetching modeReport with id: {}", id);
        ModeReportResponse modeReport = modeReportService.get(id);
        log.debug("Fetched modeReport: {}", modeReport);
        return ResponseEntity.ok(modeReport);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ModeReportResponse> createModeReport(@RequestBody ModeReportRequest modeReportRequest) {
        log.info("Creating new modeReport: {}", modeReportRequest);
        ModeReportResponse createdModeReport = modeReportService.create(modeReportRequest);
        log.debug("Created modeReport: {}", createdModeReport);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdModeReport);
    }

    @PostMapping(value = "/{id}/upload")
    public ResponseEntity<?> uploadReport(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        log.info("Uploading new file: {}", file.getOriginalFilename());
        boolean success = modeReportService.uploadFile(id, file);
        if (success) {
            log.debug("File uploaded successfully: {}", file.getOriginalFilename());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            log.error("Failed to upload file: {}", file.getOriginalFilename());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Message("error", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to upload file"));
        }
    }

    @Override
    public ResponseEntity<Resource> downloadReport(Long id) {
        File file = modeReportService.getFile(id);
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ModeReportResponse> updateModeReport(@PathVariable Long id, @RequestBody ModeReportRequest modeReportRequest) {
        log.info("Updating modeReport with id: {}", id);
        ModeReportResponse updatedModeReport = modeReportService.update(id, modeReportRequest);
        log.debug("Updated modeReport: {}", updatedModeReport);
        return ResponseEntity.ok(updatedModeReport);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteModeReport(@PathVariable Long id) {
        log.info("Deleting modeReport with id: {}", id);
        modeReportService.delete(id);
        log.debug("ModeReport with id: {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<ModeReportResponse>> getPageModeReports(@RequestBody PageRequest pageRequest) {
        log.info("Fetching page modeReports with filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());
        Page<ModeReportResponse> modeReports = modeReportService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );
        log.debug("Fetched modeReports page: {}", modeReports);
        return ResponseEntity.ok(modeReports);
    }

    @PostMapping(value = "/fetch/{taxpayerNumber}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<ModeReportResponse>> getPageModeReports(@RequestBody PageRequest pageRequest, @PathVariable String taxpayerNumber) {
        Map<String, String> filters = pageRequest.getFilters();
        filters.put("taxpayerNumber", taxpayerNumber);
        pageRequest.setFilters(filters);
        log.info("Fetching page modeReports with filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());
        Page<ModeReportResponse> modeReports = modeReportService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );
        log.debug("Fetched modeReports page: {}", modeReports);
        return ResponseEntity.ok(modeReports);
    }
}

