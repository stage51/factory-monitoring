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
import ru.centrikt.factorymonitoringservice.application.mappers.daily.DailyReportMapper;
import ru.centrikt.factorymonitoringservice.application.services.DailyReportService;
import ru.centrikt.factorymonitoringservice.application.utils.filter.FilterUtil;
import ru.centrikt.factorymonitoringservice.application.utils.ftp.FTPReportType;
import ru.centrikt.factorymonitoringservice.application.utils.ftp.FTPUtil;
import ru.centrikt.factorymonitoringservice.application.utils.validator.EntityValidator;
import ru.centrikt.factorymonitoringservice.domain.enums.ReportStatus;
import ru.centrikt.factorymonitoringservice.domain.enums.Status;
import ru.centrikt.factorymonitoringservice.domain.exceptions.EntityNotFoundException;
import ru.centrikt.factorymonitoringservice.domain.exceptions.FileDownloadException;
import ru.centrikt.factorymonitoringservice.domain.exceptions.MessageSendingException;
import ru.centrikt.factorymonitoringservice.domain.models.daily.*;
import ru.centrikt.factorymonitoringservice.presentation.dtos.messages.ReportMessage;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.daily.DailyReportRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.ReportStatusResponse;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.daily.DailyReportResponse;

import java.io.File;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DailyReportServiceImpl implements DailyReportService {

    private DailyReportRepository dailyReportRepository;
    private EntityValidator entityValidator;
    private FilterUtil<DailyReport> filterUtil;
    private TimingConfig timingConfig;
    private RabbitTemplate rabbitTemplate;
    private FTPUtil ftpUtil;

    public DailyReportServiceImpl(EntityValidator entityValidator,
                                  FilterUtil<DailyReport> filterUtil, TimingConfig timingConfig,
                                  RabbitTemplate rabbitTemplate, DailyReportRepository dailyReportRepository,
                                  FTPUtil ftpUtil) {
        this.dailyReportRepository = dailyReportRepository;
        this.entityValidator = entityValidator;
        this.filterUtil = filterUtil;
        this.timingConfig = timingConfig;
        this.rabbitTemplate = rabbitTemplate;
        this.ftpUtil = ftpUtil;
        log.info("DailyReportServiceImpl initialized");
    }
    @Autowired
    public void setDailyReportRepository(DailyReportRepository dailyReportRepository) {
        this.dailyReportRepository = dailyReportRepository;
        log.debug("DailyReportRepository set");
    }

    @Autowired
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
        log.debug("EntityValidator set");
    }

    @Autowired
    public void setFilterUtil(FilterUtil<DailyReport> filterUtil) {
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
    public DailyReportResponse create(DailyReportRequest dto) {
        log.trace("Entering create method with dto: {}", dto);
        entityValidator.validate(dto);
        log.debug("Validated dto: {}", dto);
        try {
            rabbitTemplate.convertAndSend("reportQueue", new ReportMessage(
                    dto.getSensor().getTaxpayerNumber(), dto.getSensor().getSensorNumber(), "Создан дневной отчет", dto.getStatus()
            ));
            log.info("Sent report message for dto: {}", dto);
        } catch (MessageSendingException e) {
            log.error("Could not send report: {}", e.getMessage());
        }
        DailyReportResponse response = DailyReportMapper.toResponse(dailyReportRepository.save(DailyReportMapper.toCreateEntity(dto)));
        log.trace("Exiting create method with response: {}", response);
        return response;
    }

    @Override
    public DailyReportResponse get(Long id) {
        log.trace("Entering get method with id: {}", id);
        DailyReportResponse response = DailyReportMapper.toResponse(dailyReportRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Daily Report not found with id: {}", id);
                    return new EntityNotFoundException("Daily Report not found with id: " + id);
                }));
        log.trace("Exiting get method with response: {}", response);
        return response;
    }

    @Override
    public DailyReportResponse update(Long id, DailyReportRequest dto) {
        log.trace("Entering update method with id: {} and dto: {}", id, dto);
        entityValidator.validate(dto);
        log.debug("Validated dto: {}", dto);
        DailyReport repoDailyReport = dailyReportRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Daily Report not found with id: " + id));
        DailyReport existingDailyReport;
        if (dto.getSensor() != null && dto.getPositions() != null) {
            existingDailyReport = DailyReportMapper.toUpdateEntity(dto,
                    repoDailyReport);
        } else {
            existingDailyReport = DailyReportMapper.toUpdateEntity(dto,
                    repoDailyReport,
                    repoDailyReport.getPositions(),
                    repoDailyReport.getSensor());
        }
        if (dto.getStatus().equals(Status.NOT_ACCEPTED_IN_RAR.toString()) || dto.getStatus().equals(Status.NOT_ACCEPTED_IN_UTM.toString())){
            try {
                rabbitTemplate.convertAndSend("reportQueue", new ReportMessage(
                        dto.getSensor().getTaxpayerNumber(), dto.getSensor().getSensorNumber(), "Не принят дневной отчет", dto.getStatus()
                ));
                log.info("Sent report message for dto: {}", dto);
            } catch (MessageSendingException e) {
                log.error("Could not send report: {}", e.getMessage());
            }
        }
        DailyReportResponse response = DailyReportMapper.toResponse(dailyReportRepository.save(existingDailyReport));
        log.trace("Exiting update method with response: {}", response);
        return response;
    }

    @Override
    public void delete(Long id) {
        log.trace("Entering delete method with id: {}", id);
        if (!dailyReportRepository.existsById(id)) {
            log.error("Daily Report not found with id: {}", id);
            throw new EntityNotFoundException("Daily Report not found with id: " + id);
        }
        dailyReportRepository.deleteById(id);
        log.info("Deleted Daily Report with id: {}", id);
    }

    @Override
    public List<DailyReportResponse> getAll() {
        log.trace("Entering getAll method");
        List<DailyReportResponse> responses = dailyReportRepository.findAll().stream()
                .map(DailyReportMapper::toResponse)
                .toList();
        log.trace("Exiting getAll method with responses: {}", responses);
        return responses;
    }

    @Override
    public Page<DailyReportResponse> getPage(int size, int number, String sortBy, String sortDirection,
                                          Map<String, String> filters, Map<String, String> dateRanges) {
        log.trace("Entering getPage method with size: {}, number: {}, sortBy: {}, sortDirection: {}, filters: {}, dateRanges: {}",
                size, number, sortBy, sortDirection, filters, dateRanges);
        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "createdAt");
        Pageable pageable = PageRequest.of(number, size, sort);
        Specification<DailyReport> specification = filterUtil.buildSpecification(filters, dateRanges);
        Page<DailyReportResponse> page = dailyReportRepository.findAll(specification, pageable).map(DailyReportMapper::toResponse);
        log.trace("Exiting getPage method with page: {}", page);
        return page;
    }

    @Override
    public boolean uploadFile(Long id, MultipartFile file) {
        DailyReport report = dailyReportRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Daily Report not found with id: {}", id);
                    return new EntityNotFoundException("Daily Report not found with id: " + id);
                });
        String localFilePath = ftpUtil.saveFileLocally(file);
        boolean success;
        if (ftpUtil.fileExists(file.getOriginalFilename(), FTPReportType.DAILY)){
            success = false;
        } else {
            success = ftpUtil.saveFileToFTP(localFilePath, file.getOriginalFilename(), FTPReportType.DAILY);
        }
        if (success) {
            report.setOriginalFilename(file.getOriginalFilename());
            dailyReportRepository.save(report);
        }
        return success;
    }

    @Override
    public File getFile(Long id) {
        DailyReport report = dailyReportRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Position not found with id: {}", id);
                    return new EntityNotFoundException("Daily Report not found with id: " + id);
                });
        if (ftpUtil.fileExists(report.getOriginalFilename(), FTPReportType.DAILY)){
            return ftpUtil.getFileFromFTP(report.getOriginalFilename(), FTPReportType.DAILY);
        } else {
            throw new FileDownloadException("File for daily report with id: " + id + " not found");
        }
    }

    @Override
    public List<ReportStatusResponse> getReportStatuses(String taxpayerNumber) {
        log.trace("Entering getReportStatuses method with taxpayerNumber: {}", taxpayerNumber);
        ZoneId zoneId = ZoneId.of(DateTimeConfig.getDefaultValue());
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        List<ReportStatusResponse> reportStatuses = dailyReportRepository.findLatestDailyReportsByTaxpayerNumber(taxpayerNumber).stream().map((e) -> {
            ReportStatusResponse reportStatusResponse = new ReportStatusResponse();
            reportStatusResponse.setControllerNumber(e.getSensor().getControllerNumber());
            reportStatusResponse.setLineNumber(e.getSensor().getLineNumber());
            reportStatusResponse.setLastReportTime(e.getCreatedAt());
            Duration duration = Duration.between(e.getCreatedAt(), now);
            if (duration.toMillis() <= timingConfig.getGreenDailyTiming()) {
                reportStatusResponse.setReportStatus(ReportStatus.OK.toString());
                log.debug("For controller {}, line {}, with last report time {}, status: {}", reportStatusResponse.getControllerNumber(), reportStatusResponse.getLineNumber(), reportStatusResponse.getLastReportTime(), reportStatusResponse.getReportStatus());
            } else if (duration.toMillis() <= timingConfig.getYellowDailyTiming()) {
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
