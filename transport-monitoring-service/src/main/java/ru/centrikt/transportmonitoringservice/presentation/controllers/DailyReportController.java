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
import ru.centrikt.transportmonitoringservice.application.services.DailyReportService;
import ru.centrikt.transportmonitoringservice.application.utils.Message;
import ru.centrikt.transportmonitoringservice.presentation.controllers.docs.AbstractDailyReportController;
import ru.centrikt.transportmonitoringservice.presentation.dtos.extra.PageRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.daily.DailyReportRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.ReportStatusResponse;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.daily.DailyReportResponse;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/v1/transport-monitoring/daily-report")
@Slf4j
public class DailyReportController implements AbstractDailyReportController {
    
    private DailyReportService dailyReportService;

    public DailyReportController(DailyReportService dailyReportService) {
        this.dailyReportService = dailyReportService;
        log.info("DailyReportController initialized with DailyReportService: {}", dailyReportService.getClass().getName());
    }

    @Autowired
    public void setDailyReportService(DailyReportService dailyReportService) {
        this.dailyReportService = dailyReportService;
        log.debug("DailyReportService set to: {}", dailyReportService.getClass().getName());
    }

    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DailyReportResponse> getDailyReport(@PathVariable Long id) {
        log.info("Fetching dailyReport with id: {}", id);
        DailyReportResponse dailyReport = dailyReportService.get(id);
        log.debug("Fetched dailyReport: {}", dailyReport);
        return ResponseEntity.ok(dailyReport);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DailyReportResponse> createDailyReport(@RequestBody DailyReportRequest dailyReportRequest) {
        log.info("Creating new dailyReport: {}", dailyReportRequest);
        DailyReportResponse createdDailyReport = dailyReportService.create(dailyReportRequest);
        log.debug("Created dailyReport: {}", createdDailyReport);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDailyReport);
    }

    @PostMapping(value = "/{id}/upload")
    public ResponseEntity<?> uploadReport(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        log.info("Uploading new file: {}", file.getOriginalFilename());
        boolean success = dailyReportService.uploadFile(id, file);
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
        File file = dailyReportService.getFile(id);
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DailyReportResponse> updateDailyReport(@PathVariable Long id, @RequestBody DailyReportRequest dailyReportRequest) {
        log.info("Updating dailyReport with id: {}", id);
        DailyReportResponse updatedDailyReport = dailyReportService.update(id, dailyReportRequest);
        log.debug("Updated dailyReport: {}", updatedDailyReport);
        return ResponseEntity.ok(updatedDailyReport);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteDailyReport(@PathVariable Long id) {
        log.info("Deleting dailyReport with id: {}", id);
        dailyReportService.delete(id);
        log.debug("DailyReport with id: {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<DailyReportResponse>> getPageDailyReports(@RequestBody PageRequest pageRequest) {
        log.info("Fetching page dailyReports with filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());
        Page<DailyReportResponse> dailyReports = dailyReportService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );
        log.debug("Fetched dailyReports page: {}", dailyReports);
        return ResponseEntity.ok(dailyReports);
    }

    @PostMapping(value = "/fetch/{taxpayerNumber}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Page<DailyReportResponse>> getPageDailyReports(@RequestBody PageRequest pageRequest, @PathVariable String taxpayerNumber) {
        Map<String, String> filters = pageRequest.getFilters();
        filters.put("taxpayerNumber", taxpayerNumber);
        pageRequest.setFilters(filters);
        log.info("Fetching page dailyReports with filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());
        Page<DailyReportResponse> dailyReports = dailyReportService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );
        log.debug("Fetched dailyReports page: {}", dailyReports);
        return ResponseEntity.ok(dailyReports);
    }
}

