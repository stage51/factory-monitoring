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
import ru.centrikt.transportmonitoringservice.application.services.NavigationReportService;
import ru.centrikt.transportmonitoringservice.application.utils.Message;
import ru.centrikt.transportmonitoringservice.presentation.controllers.docs.AbstractNavigationReportController;
import ru.centrikt.transportmonitoringservice.presentation.dtos.extra.PageRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.navigation.*;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.ReportStatusResponse;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.navigation.NavigationReportResponse;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/v1/factory-monitoring/five-minute-report")
@Slf4j
public class NavigateReportController implements AbstractNavigationReportController {
    
    private NavigationReportService navigationReportService;

    public NavigateReportController(NavigationReportService navigationReportService) {
        this.navigationReportService = navigationReportService;
        log.info("NavigationReportController initialized with NavigationReportService: {}", navigationReportService.getClass().getName());
    }

    @Autowired
    public void setNavigationReportService(NavigationReportService navigationReportService) {
        this.navigationReportService = navigationReportService;
        log.debug("NavigationReportService set to: {}", navigationReportService.getClass().getName());
    }

    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<NavigationReportResponse> getNavigationReport(@PathVariable Long id) {
        log.info("Fetching navigationReport with id: {}", id);
        NavigationReportResponse navigationReport = navigationReportService.get(id);
        log.debug("Fetched navigationReport: {}", navigationReport);
        return ResponseEntity.ok(navigationReport);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<NavigationReportResponse> createNavigationReport(@RequestBody NavigationReportRequest navigationReportRequest) {
        log.info("Creating new navigationReport: {}", navigationReportRequest);
        NavigationReportResponse createdNavigationReport = navigationReportService.create(navigationReportRequest);
        log.debug("Created navigationReport: {}", createdNavigationReport);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNavigationReport);
    }

    @PostMapping(value = "/{id}/upload")
    public ResponseEntity<?> uploadReport(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        log.info("Uploading new file: {}", file.getOriginalFilename());
        boolean success = navigationReportService.uploadFile(id, file);
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
        File file = navigationReportService.getFile(id);
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<NavigationReportResponse> updateNavigationReport(@PathVariable Long id, @RequestBody NavigationReportRequest navigationReportRequest) {
        log.info("Updating navigationReport with id: {}", id);
        NavigationReportResponse updatedNavigationReport = navigationReportService.update(id, navigationReportRequest);
        log.debug("Updated navigationReport: {}", updatedNavigationReport);
        return ResponseEntity.ok(updatedNavigationReport);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteNavigationReport(@PathVariable Long id) {
        log.info("Deleting navigationReport with id: {}", id);
        navigationReportService.delete(id);
        log.debug("NavigationReport with id: {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<NavigationReportResponse>> getPageNavigationReports(@RequestBody PageRequest pageRequest) {
        log.info("Fetching page navigationReports with filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());
        Page<NavigationReportResponse> navigationReports = navigationReportService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );
        log.debug("Fetched navigationReports page: {}", navigationReports);
        return ResponseEntity.ok(navigationReports);
    }

    @PostMapping(value = "/fetch/{taxpayerNumber}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Page<NavigationReportResponse>> getPageNavigationReports(@RequestBody PageRequest pageRequest, @PathVariable String taxpayerNumber) {
        Map<String, String> filters = pageRequest.getFilters();
        filters.put("taxpayerNumber", taxpayerNumber);
        pageRequest.setFilters(filters);
        log.info("Fetching page navigationReports with filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());
        Page<NavigationReportResponse> navigationReports = navigationReportService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );
        log.debug("Fetched navigationReports page: {}", navigationReports);
        return ResponseEntity.ok(navigationReports);
    }

    @PostMapping(value = "/navigate")
    public ResponseEntity<List<NavigationReportResponse>> navigate(@RequestBody NavigationAdminNavigateParamsRequest paramsRequest) {
        log.info("Navigate with admin params: {}", paramsRequest);
        List<NavigationReportResponse> list = navigationReportService.navigate(paramsRequest);
        log.debug("Result of admin navigation: {}", list);
        return ResponseEntity.ok(list);
    }

    @PostMapping(value = "/navigate/{taxpayerNumber}")
    public ResponseEntity<List<NavigationReportResponse>> navigate(@RequestBody NavigationUserNavigateParamsRequest paramsRequest, @PathVariable String taxpayerNumber) {
        log.info("Navigate with user params: {}", paramsRequest);
        List<NavigationReportResponse> list = navigationReportService.navigate(taxpayerNumber, paramsRequest);
        log.debug("Result of user navigation: {}", list);
        return ResponseEntity.ok(list);
    }

    @PostMapping(value = "/find")
    public ResponseEntity<NavigationReportResponse> find(@RequestBody NavigationAdminFindParamsRequest paramsRequest) {
        log.info("Find with admin params: {}", paramsRequest);
        NavigationReportResponse response = navigationReportService.find(paramsRequest);
        log.debug("Result of admin finding: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/find/{taxpayerNumber}")
    public ResponseEntity<NavigationReportResponse> find(@RequestBody NavigationUserFindParamsRequest paramsRequest, @PathVariable String taxpayerNumber) {
        log.info("Find with user params: {}", paramsRequest);
        NavigationReportResponse response = navigationReportService.find(taxpayerNumber, paramsRequest);
        log.debug("Result of user finding: {}", response);
        return ResponseEntity.ok(response);
    }
}

