package ru.centrikt.transportmonitoringservice.application.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.centrikt.transportmonitoringservice.application.configs.TimingConfig;
import ru.centrikt.transportmonitoringservice.application.mappers.navigation.NavigationReportMapper;
import ru.centrikt.transportmonitoringservice.application.services.NavigationReportService;
import ru.centrikt.transportmonitoringservice.application.utils.filter.FilterUtil;
import ru.centrikt.transportmonitoringservice.application.utils.ftp.FTPReportType;
import ru.centrikt.transportmonitoringservice.application.utils.ftp.FTPUtil;
import ru.centrikt.transportmonitoringservice.application.utils.validator.EntityValidator;
import ru.centrikt.transportmonitoringservice.domain.enums.Status;
import ru.centrikt.transportmonitoringservice.domain.exceptions.EntityNotFoundException;
import ru.centrikt.transportmonitoringservice.domain.exceptions.FileDownloadException;
import ru.centrikt.transportmonitoringservice.domain.exceptions.MessageSendingException;
import ru.centrikt.transportmonitoringservice.domain.models.navigation.NavigationReport;
import ru.centrikt.transportmonitoringservice.domain.models.navigation.NavigationReportRepository;
import ru.centrikt.transportmonitoringservice.presentation.dtos.messages.ReportMessage;
import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.navigation.*;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.navigation.NavigationReportResponse;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NavigationReportServiceImpl implements NavigationReportService {
    
    private NavigationReportRepository navigationReportRepository;
    private EntityValidator entityValidator;
    private FilterUtil<NavigationReport> filterUtil;
    private TimingConfig timingConfig;
    private RabbitTemplate rabbitTemplate;
    private FTPUtil ftpUtil;

    public NavigationReportServiceImpl(NavigationReportRepository navigationReportRepository,
                                       EntityValidator entityValidator,
                                       FilterUtil<NavigationReport> filterUtil, TimingConfig timingConfig,
                                       RabbitTemplate rabbitTemplate, FTPUtil ftpUtil) {
        this.navigationReportRepository = navigationReportRepository;
        this.entityValidator = entityValidator;
        this.filterUtil = filterUtil;
        this.timingConfig = timingConfig;
        this.rabbitTemplate = rabbitTemplate;
        this.ftpUtil = ftpUtil;
        log.info("NavigationReportServiceImpl initialized");
    }

    @Autowired
    public void setNavigationReportRepository(NavigationReportRepository navigationReportRepository) {
        this.navigationReportRepository = navigationReportRepository;
        log.debug("NavigationReportRepository set");
    }

    @Autowired
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
        log.debug("EntityValidator set");
    }

    @Autowired
    public void setFilterUtil(FilterUtil<NavigationReport> filterUtil) {
        this.filterUtil = filterUtil;
        log.debug("FilterUtil set");
    }

    @Autowired
    public void setTimingConfig(TimingConfig timingConfig) {
        this.timingConfig = timingConfig;
        log.debug("TimingConfig set");
    }

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        log.debug("RabbitTemplate set");
    }

    @Autowired
    public void setFtpUtil(FTPUtil ftpUtil) {
        this.ftpUtil = ftpUtil;
    }

    @Override
    public NavigationReportResponse create(NavigationReportRequest dto) {
        log.trace("Entering create method with dto: {}", dto);
        entityValidator.validate(dto);
        log.debug("Validated dto: {}", dto);
        try {
            rabbitTemplate.convertAndSend("reportQueue", new ReportMessage(
                    dto.getSensor().getTaxpayerNumber(), dto.getSensor().getGovNumber(), "Создан пятиминутный отчет", dto.getStatus()
            ));
            log.info("Sent report message for dto: {}", dto);
        } catch (MessageSendingException e) {
            log.error("Could not send report: {}", e.getMessage());
        }
        NavigationReportResponse response = NavigationReportMapper.toResponse(navigationReportRepository.save(NavigationReportMapper.toCreateEntity(dto)));
        log.trace("Exiting create method with response: {}", response);
        return response;
    }

    @Override
    public NavigationReportResponse get(Long id) {
        log.trace("Entering get method with id: {}", id);
        NavigationReportResponse response = NavigationReportMapper.toResponse(navigationReportRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("NavigationReport not found with id: {}", id);
                    return new EntityNotFoundException("NavigationReport not found with id: " + id);
                }));
        log.trace("Exiting get method with response: {}", response);
        return response;
    }

    @Override
    public NavigationReportResponse update(Long id, NavigationReportRequest dto) {
        log.trace("Entering update method with id: {} and dto: {}", id, dto);
        entityValidator.validate(dto);
        log.debug("Validated dto: {}", dto);
        NavigationReport repoNavigationReport = navigationReportRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("NavigationReport not found with id: " + id));
        NavigationReport existingNavigationReport;
        if (dto.getSensor() != null && dto.getLevelGauges() != null && dto.getNavigationData() != null) {
            existingNavigationReport = NavigationReportMapper.toUpdateEntity(dto,
                    repoNavigationReport);
        } else {
            existingNavigationReport = NavigationReportMapper.toUpdateEntity(dto,
                    repoNavigationReport,
                    repoNavigationReport.getLevelGauges(),
                    repoNavigationReport.getData(),
                    repoNavigationReport.getSensor());
        }
        if (dto.getStatus().equals(Status.NOT_ACCEPTED_IN_RAR.toString()) || dto.getStatus().equals(Status.NOT_ACCEPTED_IN_UTM.toString())){
            try {
                rabbitTemplate.convertAndSend("reportQueue", new ReportMessage(
                        dto.getSensor().getTaxpayerNumber(), dto.getSensor().getGovNumber(), "Не принят пятиминутный отчет", dto.getStatus()
                ));
                log.info("Sent report message for dto: {}", dto);
            } catch (MessageSendingException e) {
                log.error("Could not send report: {}", e.getMessage());
            }
        }
        NavigationReportResponse response = NavigationReportMapper.toResponse(navigationReportRepository.save(existingNavigationReport));
        log.trace("Exiting update method with response: {}", response);
        return response;
    }

    @Override
    public void delete(Long id) {
        log.trace("Entering delete method with id: {}", id);
        if (!navigationReportRepository.existsById(id)) {
            log.error("NavigationReport not found with id: {}", id);
            throw new EntityNotFoundException("NavigationReport not found with id: " + id);
        }
        navigationReportRepository.deleteById(id);
        log.info("Deleted navigationReport with id: {}", id);
    }

    @Override
    public List<NavigationReportResponse> navigate(String taxpayerNumber, NavigationUserNavigateParamsRequest paramsRequest) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "data.navigationDate");
        Map<String, String> filters = new HashMap<>();
        filters.put("taxpayerNumber", taxpayerNumber);
        filters.put("govNumber", paramsRequest.getGovNumber());
        Map<String, String> dateRanges = new HashMap<>();
        dateRanges.put("navigationDate", paramsRequest.getNavigationDate());
        Specification<NavigationReport> specification = filterUtil.buildSpecification(filters, dateRanges);
        List<NavigationReportResponse> list = navigationReportRepository.findAll(specification, sort).stream().map(NavigationReportMapper::toResponse).toList();
        return list;
    }

    @Override
    public List<NavigationReportResponse> navigate(NavigationAdminNavigateParamsRequest paramsRequest) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "data.navigationDate");
        Map<String, String> filters = new HashMap<>();
        filters.put("organizationName", paramsRequest.getOrganizationName());
        filters.put("govNumber", paramsRequest.getGovNumber());
        Map<String, String> dateRanges = new HashMap<>();
        dateRanges.put("navigationDate", paramsRequest.getNavigationDate());
        Specification<NavigationReport> specification = filterUtil.buildSpecification(filters, dateRanges);
        List<NavigationReportResponse> list = navigationReportRepository.findAll(specification, sort).stream().map(NavigationReportMapper::toResponse).toList();
        return list;
    }

    @Override
    public NavigationReportResponse find(String taxpayerNumber, NavigationUserFindParamsRequest paramsRequest) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "data.navigationDate");
        Map<String, String> filters = new HashMap<>();
        filters.put("taxpayerNumber", taxpayerNumber);
        filters.put("govNumber", paramsRequest.getGovNumber());
        Map<String, String> dateRanges = new HashMap<>();
        Specification<NavigationReport> specification = filterUtil.buildSpecification(filters, dateRanges);
        NavigationReportResponse report = NavigationReportMapper.toResponse(
                navigationReportRepository.findFirstByDataNavigationDate(specification, sort)
                        .orElseThrow(() -> new EntityNotFoundException("Navigation Report not found")));
        return report;
    }

    @Override
    public NavigationReportResponse find(NavigationAdminFindParamsRequest paramsRequest) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "data.navigationDate");
        Map<String, String> filters = new HashMap<>();
        filters.put("organizationName", paramsRequest.getOrganizationName());
        filters.put("govNumber", paramsRequest.getGovNumber());
        Map<String, String> dateRanges = new HashMap<>();
        Specification<NavigationReport> specification = filterUtil.buildSpecification(filters, dateRanges);
        NavigationReportResponse report = NavigationReportMapper.toResponse(
                navigationReportRepository.findFirstByDataNavigationDate(specification, sort)
                        .orElseThrow(() -> new EntityNotFoundException("Navigation Report not found")));
        return report;
    }

    @Override
    public List<NavigationReportResponse> getAll() {
        log.trace("Entering getAll method");
        List<NavigationReportResponse> responses = navigationReportRepository.findAll().stream()
                .map(NavigationReportMapper::toResponse)
                .toList();
        log.trace("Exiting getAll method with responses: {}", responses);
        return responses;
    }

    @Override
    public Page<NavigationReportResponse> getPage(int size, int number, String sortBy, String sortDirection,
                                          Map<String, String> filters, Map<String, String> dateRanges) {
        log.trace("Entering getPage method with size: {}, number: {}, sortBy: {}, sortDirection: {}, filters: {}, dateRanges: {}",
                size, number, sortBy, sortDirection, filters, dateRanges);
        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "createdAt");
        Pageable pageable = PageRequest.of(number, size, sort);
        Specification<NavigationReport> specification = filterUtil.buildSpecification(filters, dateRanges);
        Page<NavigationReportResponse> page = navigationReportRepository.findAll(specification, pageable).map(NavigationReportMapper::toResponse);
        log.trace("Exiting getPage method with page: {}", page);
        return page;
    }

    @Override
    public boolean uploadFile(Long id, MultipartFile file) {
        NavigationReport report = navigationReportRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("NavigationReport not found with id: {}", id);
                    return new EntityNotFoundException("NavigationReport not found with id: " + id);
                });
        String localFilePath = ftpUtil.saveFileLocally(file);
        boolean success;
        if (ftpUtil.fileExists(file.getOriginalFilename(), FTPReportType.FIVEMINUTE)){
            success = false;
        } else {
            success = ftpUtil.saveFileToFTP(localFilePath, file.getOriginalFilename(), FTPReportType.FIVEMINUTE);
        }
        if (success) {
            report.setOriginalFilename(file.getOriginalFilename());
            navigationReportRepository.save(report);
        }
        return success;
    }

    @Override
    public File getFile(Long id) {
        NavigationReport report = navigationReportRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("NavigationReport not found with id: {}", id);
                    return new EntityNotFoundException("NavigationReport not found with id: " + id);
                });
        if (ftpUtil.fileExists(report.getOriginalFilename(), FTPReportType.FIVEMINUTE)){
            return ftpUtil.getFileFromFTP(report.getOriginalFilename(), FTPReportType.FIVEMINUTE);
        } else {
            throw new FileDownloadException("File for five minute report with id: " + id + " not found");
        }
    }


}

