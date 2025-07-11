package ru.centrikt.factorymonitoringservice.application.services.impl;

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
import ru.centrikt.factorymonitoringservice.application.configs.DateTimeConfig;
import ru.centrikt.factorymonitoringservice.application.configs.TimingConfig;
import ru.centrikt.factorymonitoringservice.application.mappers.fiveminute.FiveMinuteReportMapper;
import ru.centrikt.factorymonitoringservice.application.services.FiveMinuteReportService;
import ru.centrikt.factorymonitoringservice.application.utils.filter.FilterUtil;
import ru.centrikt.factorymonitoringservice.application.utils.ftp.FTPReportType;
import ru.centrikt.factorymonitoringservice.application.utils.ftp.FTPUtil;
import ru.centrikt.factorymonitoringservice.application.utils.validator.EntityValidator;
import ru.centrikt.factorymonitoringservice.domain.enums.ReportStatus;
import ru.centrikt.factorymonitoringservice.domain.enums.Status;
import ru.centrikt.factorymonitoringservice.domain.exceptions.EntityNotFoundException;
import ru.centrikt.factorymonitoringservice.domain.exceptions.FileDownloadException;
import ru.centrikt.factorymonitoringservice.domain.exceptions.MessageSendingException;
import ru.centrikt.factorymonitoringservice.domain.models.fiveminute.FiveMinuteReport;
import ru.centrikt.factorymonitoringservice.domain.models.fiveminute.FiveMinuteReportRepository;
import ru.centrikt.factorymonitoringservice.presentation.dtos.messages.ReportMessage;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.fiveminute.FiveMinuteReportRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.ReportStatusResponse;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.fiveminute.FiveMinuteReportResponse;

import java.io.File;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FiveMinuteReportServiceImpl implements FiveMinuteReportService {
    
    private FiveMinuteReportRepository fiveMinuteReportRepository;
    private EntityValidator entityValidator;
    private FilterUtil<FiveMinuteReport> filterUtil;
    private TimingConfig timingConfig;
    private RabbitTemplate rabbitTemplate;
    private FTPUtil ftpUtil;

    public FiveMinuteReportServiceImpl(FiveMinuteReportRepository fiveMinuteReportRepository,
                                       EntityValidator entityValidator,
                                       FilterUtil<FiveMinuteReport> filterUtil, TimingConfig timingConfig,
                                       RabbitTemplate rabbitTemplate, FTPUtil ftpUtil) {
        this.fiveMinuteReportRepository = fiveMinuteReportRepository;
        this.entityValidator = entityValidator;
        this.filterUtil = filterUtil;
        this.timingConfig = timingConfig;
        this.rabbitTemplate = rabbitTemplate;
        this.ftpUtil = ftpUtil;
        log.info("FiveMinuteReportServiceImpl initialized");
    }

    @Autowired
    public void setFiveMinuteReportRepository(FiveMinuteReportRepository fiveMinuteReportRepository) {
        this.fiveMinuteReportRepository = fiveMinuteReportRepository;
        log.debug("FiveMinuteReportRepository set");
    }

    @Autowired
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
        log.debug("EntityValidator set");
    }

    @Autowired
    public void setFilterUtil(FilterUtil<FiveMinuteReport> filterUtil) {
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
    public FiveMinuteReportResponse create(FiveMinuteReportRequest dto) {
        log.trace("Entering create method with dto: {}", dto);
        entityValidator.validate(dto);
        log.debug("Validated dto: {}", dto);
        try {
            rabbitTemplate.convertAndSend("reportQueue", new ReportMessage(
                    dto.getSensor().getTaxpayerNumber(), dto.getSensor().getSensorNumber(), "Создан пятиминутный отчет", dto.getStatus()
            ));
            log.info("Sent report message for dto: {}", dto);
        } catch (MessageSendingException e) {
            log.error("Could not send report: {}", e.getMessage());
        }
        FiveMinuteReportResponse response = FiveMinuteReportMapper.toResponse(fiveMinuteReportRepository.save(FiveMinuteReportMapper.toCreateEntity(dto)));
        log.trace("Exiting create method with response: {}", response);
        return response;
    }

    @Override
    public FiveMinuteReportResponse get(Long id) {
        log.trace("Entering get method with id: {}", id);
        FiveMinuteReportResponse response = FiveMinuteReportMapper.toResponse(fiveMinuteReportRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("FiveMinuteReport not found with id: {}", id);
                    return new EntityNotFoundException("FiveMinuteReport not found with id: " + id);
                }));
        log.trace("Exiting get method with response: {}", response);
        return response;
    }

    @Override
    public FiveMinuteReportResponse update(Long id, FiveMinuteReportRequest dto) {
        log.trace("Entering update method with id: {} and dto: {}", id, dto);
        entityValidator.validate(dto);
        log.debug("Validated dto: {}", dto);
        FiveMinuteReport repoFiveMinuteReport = fiveMinuteReportRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("FiveMinuteReport not found with id: " + id));
        FiveMinuteReport existingFiveMinuteReport;
        if (dto.getSensor() != null && dto.getPosition() != null) {
            existingFiveMinuteReport = FiveMinuteReportMapper.toUpdateEntity(dto,
                    repoFiveMinuteReport);
        } else {
            existingFiveMinuteReport = FiveMinuteReportMapper.toUpdateEntity(dto,
                    repoFiveMinuteReport,
                    repoFiveMinuteReport.getPosition(),
                    repoFiveMinuteReport.getSensor());
        }
        if (dto.getStatus().equals(Status.NOT_ACCEPTED_IN_RAR.toString()) || dto.getStatus().equals(Status.NOT_ACCEPTED_IN_UTM.toString())){
            try {
                rabbitTemplate.convertAndSend("reportQueue", new ReportMessage(
                        dto.getSensor().getTaxpayerNumber(), dto.getSensor().getSensorNumber(), "Не принят пятиминутный отчет", dto.getStatus()
                ));
                log.info("Sent report message for dto: {}", dto);
            } catch (MessageSendingException e) {
                log.error("Could not send report: {}", e.getMessage());
            }
        }
        FiveMinuteReportResponse response = FiveMinuteReportMapper.toResponse(fiveMinuteReportRepository.save(existingFiveMinuteReport));
        log.trace("Exiting update method with response: {}", response);
        return response;
    }

    @Override
    public void delete(Long id) {
        log.trace("Entering delete method with id: {}", id);
        if (!fiveMinuteReportRepository.existsById(id)) {
            log.error("FiveMinuteReport not found with id: {}", id);
            throw new EntityNotFoundException("FiveMinuteReport not found with id: " + id);
        }
        fiveMinuteReportRepository.deleteById(id);
        log.info("Deleted fiveMinuteReport with id: {}", id);
    }

    @Override
    public List<FiveMinuteReportResponse> getAll() {
        log.trace("Entering getAll method");
        List<FiveMinuteReportResponse> responses = fiveMinuteReportRepository.findAll().stream()
                .map(FiveMinuteReportMapper::toResponse)
                .toList();
        log.trace("Exiting getAll method with responses: {}", responses);
        return responses;
    }

    @Override
    public Page<FiveMinuteReportResponse> getPage(int size, int number, String sortBy, String sortDirection,
                                          Map<String, String> filters, Map<String, String> dateRanges) {
        log.trace("Entering getPage method with size: {}, number: {}, sortBy: {}, sortDirection: {}, filters: {}, dateRanges: {}",
                size, number, sortBy, sortDirection, filters, dateRanges);
        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "createdAt");
        Pageable pageable = PageRequest.of(number, size, sort);
        Specification<FiveMinuteReport> specification = filterUtil.buildSpecification(filters, dateRanges);
        Page<FiveMinuteReportResponse> page = fiveMinuteReportRepository.findAll(specification, pageable).map(FiveMinuteReportMapper::toResponse);
        log.trace("Exiting getPage method with page: {}", page);
        return page;
    }

    @Override
    public boolean uploadFile(Long id, MultipartFile file) {
        FiveMinuteReport report = fiveMinuteReportRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("FiveMinuteReport not found with id: {}", id);
                    return new EntityNotFoundException("FiveMinuteReport not found with id: " + id);
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
            fiveMinuteReportRepository.save(report);
        }
        return success;
    }

    @Override
    public File getFile(Long id) {
        FiveMinuteReport report = fiveMinuteReportRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("FiveMinuteReport not found with id: {}", id);
                    return new EntityNotFoundException("FiveMinuteReport not found with id: " + id);
                });
        if (ftpUtil.fileExists(report.getOriginalFilename(), FTPReportType.FIVEMINUTE)){
            return ftpUtil.getFileFromFTP(report.getOriginalFilename(), FTPReportType.FIVEMINUTE);
        } else {
            throw new FileDownloadException("File for five minute report with id: " + id + " not found");
        }
    }

    @Override
    public List<ReportStatusResponse> getReportStatuses(String taxpayerNumber) {
        log.trace("Entering getReportStatuses method with taxpayerNumber: {}", taxpayerNumber);
        ZoneId zoneId = ZoneId.of(DateTimeConfig.getDefaultValue());
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        List<ReportStatusResponse> reportStatuses = fiveMinuteReportRepository.findLatestFiveMinuteReportsByTaxpayerNumber(taxpayerNumber).stream().map((e) -> {
            ReportStatusResponse reportStatusResponse = new ReportStatusResponse();
            reportStatusResponse.setControllerNumber(e.getSensor().getControllerNumber());
            reportStatusResponse.setLineNumber(e.getSensor().getLineNumber());
            reportStatusResponse.setLastReportTime(e.getCreatedAt());
            Duration duration = Duration.between(e.getCreatedAt(), now);
            if (duration.toMillis() <= timingConfig.getGreenFiveminuteTiming()) {
                reportStatusResponse.setReportStatus(ReportStatus.OK.toString());
                log.debug("For controller {}, line {}, with last report time {}, status: {}", reportStatusResponse.getControllerNumber(), reportStatusResponse.getLineNumber(), reportStatusResponse.getLastReportTime(), reportStatusResponse.getReportStatus());
            } else if (duration.toMillis() <= timingConfig.getYellowFiveminuteTiming()) {
                reportStatusResponse.setReportStatus(ReportStatus.WARN.toString());
                log.debug("For controller {}, line {}, with last report time {}, status: {}", reportStatusResponse.getControllerNumber(), reportStatusResponse.getLineNumber(), reportStatusResponse.getLastReportTime(), reportStatusResponse.getReportStatus());
            } else {
                reportStatusResponse.setReportStatus(ReportStatus.ERROR.toString());
                log.debug("For controller {}, line {}, with last report time {}, status: {}", reportStatusResponse.getControllerNumber(), reportStatusResponse.getLineNumber(), reportStatusResponse.getLastReportTime(), reportStatusResponse.getReportStatus());
            }
            return reportStatusResponse;
        }).collect(Collectors.toList());

        log.trace("Exiting getReportStatuses method with reportStatuses: {}", reportStatuses);
        return reportStatuses;
    }
}

