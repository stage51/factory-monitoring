package centrikt.factory_monitoring.five_minute_report.services.impl;

import centrikt.factory_monitoring.five_minute_report.configs.DateTimeConfig;
import centrikt.factory_monitoring.five_minute_report.configs.TimingConfig;
import centrikt.factory_monitoring.five_minute_report.dtos.messages.ReportMessage;
import centrikt.factory_monitoring.five_minute_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.ReportStatusResponse;
import centrikt.factory_monitoring.five_minute_report.enums.ReportStatus;
import centrikt.factory_monitoring.five_minute_report.enums.Status;
import centrikt.factory_monitoring.five_minute_report.exceptions.EntityNotFoundException;
import centrikt.factory_monitoring.five_minute_report.exceptions.MessageSendingException;
import centrikt.factory_monitoring.five_minute_report.mappers.PositionMapper;
import centrikt.factory_monitoring.five_minute_report.models.Position;
import centrikt.factory_monitoring.five_minute_report.repos.PositionRepository;
import centrikt.factory_monitoring.five_minute_report.repos.ProductRepository;
import centrikt.factory_monitoring.five_minute_report.services.PositionService;
import centrikt.factory_monitoring.five_minute_report.utils.filter.FilterUtil;
import centrikt.factory_monitoring.five_minute_report.utils.validator.EntityValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PositionServiceImpl implements PositionService {

    private PositionRepository positionRepository;
    private ProductRepository productRepository;
    private EntityValidator entityValidator;
    private FilterUtil<Position> filterUtil;
    private TimingConfig timingConfig;
    private RabbitTemplate rabbitTemplate;

    public PositionServiceImpl(PositionRepository positionRepository,
                               ProductRepository productRepository,
                               EntityValidator entityValidator,
                               FilterUtil<Position> filterUtil, TimingConfig timingConfig,
                               RabbitTemplate rabbitTemplate) {
        this.positionRepository = positionRepository;
        this.productRepository = productRepository;
        this.entityValidator = entityValidator;
        this.filterUtil = filterUtil;
        this.timingConfig = timingConfig;
        this.rabbitTemplate = rabbitTemplate;
        log.info("PositionServiceImpl initialized");
    }

    @Autowired
    public void setPositionRepository(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
        log.debug("PositionRepository set");
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
        log.debug("EntityValidator set");
    }

    @Autowired
    public void setFilterUtil(FilterUtil<Position> filterUtil) {
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

    @Override
    public PositionResponse create(PositionRequest dto) {
        log.trace("Entering create method with dto: {}", dto);
        entityValidator.validate(dto);
        log.debug("Validated dto: {}", dto);
        try {
            rabbitTemplate.convertAndSend("reportQueue", new ReportMessage(
                    dto.getTaxpayerNumber(), dto.getSensorNumber(), "Создан пятиминутный отчет", dto.getStatus()
            ));
            log.info("Sent report message for dto: {}", dto);
        } catch (MessageSendingException e) {
            log.error("Could not send report: {}", e.getMessage());
        }
        PositionResponse response = PositionMapper.toResponse(positionRepository.save(PositionMapper.toEntity(dto)));
        log.trace("Exiting create method with response: {}", response);
        return response;
    }

    @Override
    public PositionResponse get(Long id) {
        log.trace("Entering get method with id: {}", id);
        PositionResponse response = PositionMapper.toResponse(positionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Position not found with id: {}", id);
                    return new EntityNotFoundException("Position not found with id: " + id);
                }));
        log.trace("Exiting get method with response: {}", response);
        return response;
    }

    @Override
    public PositionResponse update(Long id, PositionRequest dto) {
        log.trace("Entering update method with id: {} and dto: {}", id, dto);
        entityValidator.validate(dto);
        log.debug("Validated dto: {}", dto);
        Position existingPosition = PositionMapper.toEntity(dto,
                positionRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Position not found with id: " + id)),
                productRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Product not found with id: " + id)
                ));
        if (existingPosition.getStatus().equals(Status.NOT_ACCEPTED_IN_RAR) || existingPosition.getStatus().equals(Status.NOT_ACCEPTED_IN_UTM)){
            try {
                rabbitTemplate.convertAndSend("reportQueue", new ReportMessage(
                        dto.getTaxpayerNumber(), dto.getSensorNumber(), "Не принят пятиминутный отчет", dto.getStatus()
                ));
                log.info("Sent report message for dto: {}", dto);
            } catch (MessageSendingException e) {
                log.error("Could not send report: {}", e.getMessage());
            }
        }
        PositionResponse response = PositionMapper.toResponse(positionRepository.save(existingPosition));
        log.trace("Exiting update method with response: {}", response);
        return response;
    }

    @Override
    public void delete(Long id) {
        log.trace("Entering delete method with id: {}", id);
        if (!positionRepository.existsById(id)) {
            log.error("Position not found with id: {}", id);
            throw new EntityNotFoundException("Position not found with id: " + id);
        }
        positionRepository.deleteById(id);
        log.info("Deleted position with id: {}", id);
    }

    @Override
    public List<PositionResponse> getAll() {
        log.trace("Entering getAll method");
        List<PositionResponse> responses = positionRepository.findAll().stream()
                .map(PositionMapper::toResponse)
                .toList();
        log.trace("Exiting getAll method with responses: {}", responses);
        return responses;
    }

    @Override
    public Page<PositionResponse> getPage(int size, int number, String sortBy, String sortDirection,
                                          Map<String, String> filters, Map<String, String> dateRanges) {
        log.trace("Entering getPage method with size: {}, number: {}, sortBy: {}, sortDirection: {}, filters: {}, dateRanges: {}",
                size, number, sortBy, sortDirection, filters, dateRanges);
        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "controlDate");
        Pageable pageable = PageRequest.of(number, size, sort);
        Specification<Position> specification = filterUtil.buildSpecification(filters, dateRanges);
        Page<PositionResponse> page = positionRepository.findAll(specification, pageable).map(PositionMapper::toResponse);
        log.trace("Exiting getPage method with page: {}", page);
        return page;
    }

    @Override
    public List<PositionResponse> createAll(List<PositionRequest> positionRequests) {
        log.trace("Entering createAll method with positionRequests: {}", positionRequests);
        List<PositionResponse> responses = positionRepository.saveAll(positionRequests.stream()
                        .map(request -> {
                            entityValidator.validate(request);
                            log.debug("Validated request: {}", request);
                            return PositionMapper.toEntity(request);
                        }).collect(Collectors.toList()))
                .stream().map(PositionMapper::toResponse)
                .collect(Collectors.toList());
        log.trace("Exiting createAll method with responses: {}", responses);
        return responses;
    }

    @Override
    public List<ReportStatusResponse> getReportStatuses(String taxpayerNumber) {
        log.trace("Entering getReportStatuses method with taxpayerNumber: {}", taxpayerNumber);
        ZoneId zoneId = ZoneId.of(DateTimeConfig.getDefaultValue());
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        List<ReportStatusResponse> reportStatuses = positionRepository.findLatestPositionsByTaxpayerNumber(taxpayerNumber).stream().map((e) -> {
            ReportStatusResponse reportStatusResponse = new ReportStatusResponse();
            reportStatusResponse.setControllerNumber(e.getControllerNumber());
            reportStatusResponse.setLineNumber(e.getLineNumber());
            reportStatusResponse.setLastReportTime(e.getControlDate());
            Duration duration = Duration.between(e.getControlDate(), now);
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

