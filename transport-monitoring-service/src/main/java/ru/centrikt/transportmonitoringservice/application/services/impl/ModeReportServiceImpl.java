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
import ru.centrikt.transportmonitoringservice.application.mappers.mode.ModeReportMapper;
import ru.centrikt.transportmonitoringservice.application.services.ModeReportService;
import ru.centrikt.transportmonitoringservice.application.utils.filter.FilterUtil;
import ru.centrikt.transportmonitoringservice.application.utils.ftp.FTPReportType;
import ru.centrikt.transportmonitoringservice.application.utils.ftp.FTPUtil;
import ru.centrikt.transportmonitoringservice.application.utils.validator.EntityValidator;
import ru.centrikt.transportmonitoringservice.domain.exceptions.EntityNotFoundException;
import ru.centrikt.transportmonitoringservice.domain.exceptions.FileDownloadException;
import ru.centrikt.transportmonitoringservice.domain.exceptions.MessageSendingException;
import ru.centrikt.transportmonitoringservice.domain.models.mode.ModeReport;
import ru.centrikt.transportmonitoringservice.domain.models.mode.ModeReportRepository;
import ru.centrikt.transportmonitoringservice.presentation.dtos.messages.ReportMessage;
import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.mode.ModeReportRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.mode.ModeReportResponse;

import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ModeReportServiceImpl implements ModeReportService {

    private ModeReportRepository modeReportRepository;
    private EntityValidator entityValidator;
    private FilterUtil<ModeReport> filterUtil;
    private RabbitTemplate rabbitTemplate;
    private FTPUtil ftpUtil;

    public ModeReportServiceImpl(ModeReportRepository modeReportRepository,
                                 EntityValidator entityValidator,
                                 FilterUtil<ModeReport> filterUtil,
                                 RabbitTemplate rabbitTemplate,
                                 FTPUtil ftpUtil) {
        this.modeReportRepository = modeReportRepository;
        this.entityValidator = entityValidator;
        this.filterUtil = filterUtil;
        this.rabbitTemplate = rabbitTemplate;
        this.ftpUtil = ftpUtil;
        log.info("ModeReportServiceImpl initialized");
    }

    @Autowired
    public void setModeReportRepository(ModeReportRepository modeReportRepository) {
        this.modeReportRepository = modeReportRepository;
        log.debug("ModeReportRepository set");
    }

    @Autowired
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
        log.debug("EntityValidator set");
    }

    @Autowired
    public void setFilterUtil(FilterUtil<ModeReport> filterUtil) {
        this.filterUtil = filterUtil;
        log.debug("FilterUtil set");
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
    public ModeReportResponse create(ModeReportRequest dto) {
        log.trace("Entering create method with dto: {}", dto);
        entityValidator.validate(dto);
        log.debug("Validated dto: {}", dto);
        try {
            rabbitTemplate.convertAndSend("reportQueue", new ReportMessage(
                    dto.getSensor().getTaxpayerNumber(), dto.getSensor().getGovNumber(), "Создана сессия", "Без статуса"
            ));
        } catch (MessageSendingException e) {
            log.error("Could not send report: {}", e.getMessage());
        }
        log.info("Sent report message for dto: {}", dto);
        ModeReportResponse response = ModeReportMapper.toResponse(modeReportRepository.save(ModeReportMapper.toCreateEntity(dto)));
        log.trace("Exiting create method with response: {}", response);
        return response;
    }

    @Override
    public ModeReportResponse get(Long id) {
        log.trace("Entering get method with id: {}", id);
        ModeReportResponse response = ModeReportMapper.toResponse(modeReportRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("ModeReport not found with id: {}", id);
                    return new EntityNotFoundException("ModeReport not found with id: " + id);
                }));
        log.trace("Exiting get method with response: {}", response);
        return response;
    }

    @Override
    public ModeReportResponse update(Long id, ModeReportRequest dto) {
        log.trace("Entering update method with id: {} and dto: {}", id, dto);
        entityValidator.validate(dto);
        log.debug("Validated dto: {}", dto);
        ModeReport repoModeReport = modeReportRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("ModeReport not found with id: " + id));
        ModeReport existingModeReport;
        if (dto.getSensor() != null && dto.getPosition() != null) {
            existingModeReport = ModeReportMapper.toUpdateEntity(
                    dto,
                    repoModeReport);

        } else {
            existingModeReport = ModeReportMapper.toUpdateEntity(
                    dto,
                    repoModeReport,
                    repoModeReport.getPosition(),
                    repoModeReport.getSensor());
        }
        ModeReportResponse response = ModeReportMapper.toResponse(modeReportRepository.save(existingModeReport));
        log.trace("Exiting update method with response: {}", response);
        return response;
    }

    @Override
    public void delete(Long id) {
        log.trace("Entering delete method with id: {}", id);
        if (!modeReportRepository.existsById(id)) {
            log.error("ModeReport not found with id: {}", id);
            throw new EntityNotFoundException("ModeReport not found with id: " + id);
        }
        modeReportRepository.deleteById(id);
        log.info("Deleted modeReport with id: {}", id);
    }

    @Override
    public List<ModeReportResponse> getAll() {
        log.trace("Entering getAll method");
        List<ModeReportResponse> responses = modeReportRepository.findAll().stream()
                .map(ModeReportMapper::toResponse)
                .toList();
        log.trace("Exiting getAll method with responses: {}", responses);
        return responses;
    }

    @Override
    public Page<ModeReportResponse> getPage(int size, int number, String sortBy, String sortDirection,
                                          Map<String, String> filters, Map<String, String> dateRanges) {
        log.trace("Entering getPage method with size: {}, number: {}, sortBy: {}, sortDirection: {}, filters: {}, dateRanges: {}",
                size, number, sortBy, sortDirection, filters, dateRanges);
        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "createdAt");
        Pageable pageable = PageRequest.of(number, size, sort);
        Specification<ModeReport> specification = filterUtil.buildSpecification(filters, dateRanges);
        Page<ModeReportResponse> page = modeReportRepository.findAll(specification, pageable).map(ModeReportMapper::toResponse);
        log.trace("Exiting getPage method with page: {}", page);
        return page;
    }

    @Override
    public boolean uploadFile(Long id, MultipartFile file) {
        ModeReport report = modeReportRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("FiveMinuteReport not found with id: {}", id);
                    return new EntityNotFoundException("FiveMinuteReport not found with id: " + id);
                });
        String localFilePath = ftpUtil.saveFileLocally(file);
        boolean success;
        if (ftpUtil.fileExists(file.getOriginalFilename(), FTPReportType.MODE)){
            success = false;
        } else {
            success = ftpUtil.saveFileToFTP(localFilePath, file.getOriginalFilename(), FTPReportType.MODE);
        }
        if (success) {
            report.setOriginalFilename(file.getOriginalFilename());
            modeReportRepository.save(report);
        }
        return success;
    }

    @Override
    public File getFile(Long id) {
        ModeReport report = modeReportRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("FiveMinuteReport not found with id: {}", id);
                    return new EntityNotFoundException("FiveMinuteReport not found with id: " + id);
                });
        if (ftpUtil.fileExists(report.getOriginalFilename(), FTPReportType.MODE)){
            return ftpUtil.getFileFromFTP(report.getOriginalFilename(), FTPReportType.MODE);
        } else {
            throw new FileDownloadException("File for mode report with id: " + id + " not found");
        }
    }
}
