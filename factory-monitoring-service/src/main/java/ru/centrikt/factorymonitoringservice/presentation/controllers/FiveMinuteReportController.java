package ru.centrikt.factorymonitoringservice.presentation.controllers;

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
import ru.centrikt.factorymonitoringservice.application.services.FiveMinuteReportService;
import ru.centrikt.factorymonitoringservice.application.utils.Message;
import ru.centrikt.factorymonitoringservice.application.utils.ftp.FTPReportType;
import ru.centrikt.factorymonitoringservice.application.utils.ftp.FTPUtil;
import ru.centrikt.factorymonitoringservice.presentation.controllers.docs.AbstractFiveMinuteReportController;
import ru.centrikt.factorymonitoringservice.presentation.dtos.extra.PageRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.fiveminute.FiveMinuteReportRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.ReportStatusResponse;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.fiveminute.FiveMinuteReportResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/v1/factory-monitoring/five-minute-report")
@Slf4j
public class FiveMinuteReportController implements AbstractFiveMinuteReportController {
    
    private FiveMinuteReportService fiveMinuteReportService;

    public FiveMinuteReportController(FiveMinuteReportService fiveMinuteReportService) {
        this.fiveMinuteReportService = fiveMinuteReportService;
        log.info("FiveMinuteReportController initialized with FiveMinuteReportService: {}", fiveMinuteReportService.getClass().getName());
    }

    @Autowired
    public void setFiveMinuteReportService(FiveMinuteReportService fiveMinuteReportService) {
        this.fiveMinuteReportService = fiveMinuteReportService;
        log.debug("FiveMinuteReportService set to: {}", fiveMinuteReportService.getClass().getName());
    }

    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<FiveMinuteReportResponse> getFiveMinuteReport(@PathVariable Long id) {
        log.info("Fetching fiveMinuteReport with id: {}", id);
        FiveMinuteReportResponse fiveMinuteReport = fiveMinuteReportService.get(id);
        log.debug("Fetched fiveMinuteReport: {}", fiveMinuteReport);
        return ResponseEntity.ok(fiveMinuteReport);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<FiveMinuteReportResponse> createFiveMinuteReport(@RequestBody FiveMinuteReportRequest fiveMinuteReportRequest) {
        log.info("Creating new fiveMinuteReport: {}", fiveMinuteReportRequest);
        FiveMinuteReportResponse createdFiveMinuteReport = fiveMinuteReportService.create(fiveMinuteReportRequest);
        log.debug("Created fiveMinuteReport: {}", createdFiveMinuteReport);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFiveMinuteReport);
    }

    @PostMapping(value = "/{id}/upload")
    public ResponseEntity<?> uploadReport(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        log.info("Uploading new file: {}", file.getOriginalFilename());
        boolean success = fiveMinuteReportService.uploadFile(id, file);
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
        File file = fiveMinuteReportService.getFile(id);
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<FiveMinuteReportResponse> updateFiveMinuteReport(@PathVariable Long id, @RequestBody FiveMinuteReportRequest fiveMinuteReportRequest) {
        log.info("Updating fiveMinuteReport with id: {}", id);
        FiveMinuteReportResponse updatedFiveMinuteReport = fiveMinuteReportService.update(id, fiveMinuteReportRequest);
        log.debug("Updated fiveMinuteReport: {}", updatedFiveMinuteReport);
        return ResponseEntity.ok(updatedFiveMinuteReport);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteFiveMinuteReport(@PathVariable Long id) {
        log.info("Deleting fiveMinuteReport with id: {}", id);
        fiveMinuteReportService.delete(id);
        log.debug("FiveMinuteReport with id: {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<FiveMinuteReportResponse>> getPageFiveMinuteReports(@RequestBody PageRequest pageRequest) {
        log.info("Fetching page fiveMinuteReports with filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());
        Page<FiveMinuteReportResponse> fiveMinuteReports = fiveMinuteReportService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );
        log.debug("Fetched fiveMinuteReports page: {}", fiveMinuteReports);
        return ResponseEntity.ok(fiveMinuteReports);
    }

    @PostMapping(value = "/fetch/{taxpayerNumber}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<FiveMinuteReportResponse>> getPageFiveMinuteReports(@RequestBody PageRequest pageRequest, @PathVariable String taxpayerNumber) {
        Map<String, String> filters = pageRequest.getFilters();
        filters.put("taxpayerNumber", taxpayerNumber);
        pageRequest.setFilters(filters);
        log.info("Fetching page fiveMinuteReports with filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());
        Page<FiveMinuteReportResponse> fiveMinuteReports = fiveMinuteReportService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );
        log.debug("Fetched fiveMinuteReports page: {}", fiveMinuteReports);
        return ResponseEntity.ok(fiveMinuteReports);
    }

    @GetMapping(value = "/check")
    public ResponseEntity<List<ReportStatusResponse>> checkLines(@RequestParam String taxpayerNumber) {
        log.debug("Checking report statuses for taxpayerNumber: {}", taxpayerNumber);
        return ResponseEntity.ok(fiveMinuteReportService.getReportStatuses(taxpayerNumber));
    }
}

