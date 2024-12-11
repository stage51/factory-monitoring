package centrikt.factory_monitoring.five_minute_report.services.impl;

import centrikt.factory_monitoring.five_minute_report.configs.DateTimeConfig;
import centrikt.factory_monitoring.five_minute_report.configs.TimingConfig;
import centrikt.factory_monitoring.five_minute_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.ReportStatusResponse;
import centrikt.factory_monitoring.five_minute_report.enums.ReportStatus;
import centrikt.factory_monitoring.five_minute_report.exceptions.EntityNotFoundException;
import centrikt.factory_monitoring.five_minute_report.mappers.PositionMapper;
import centrikt.factory_monitoring.five_minute_report.models.Position;
import centrikt.factory_monitoring.five_minute_report.repos.PositionRepository;
import centrikt.factory_monitoring.five_minute_report.services.PositionService;
import centrikt.factory_monitoring.five_minute_report.utils.filter.FilterUtil;
import centrikt.factory_monitoring.five_minute_report.utils.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PositionServiceImpl implements PositionService {

    private PositionRepository positionRepository;
    private EntityValidator entityValidator;
    private FilterUtil<Position> filterUtil;
    private TimingConfig timingConfig;

    public PositionServiceImpl(PositionRepository positionRepository, EntityValidator entityValidator,
                               FilterUtil<Position> filterUtil, TimingConfig timingConfig) {
        this.positionRepository = positionRepository;
        this.entityValidator = entityValidator;
        this.filterUtil = filterUtil;
        this.timingConfig = timingConfig;
    }

    @Autowired
    public void setPositionRepository(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Autowired
    public void setEntityValidator(EntityValidator entityValidator) {
        this.entityValidator = entityValidator;
    }

    @Autowired
    public void setFilterUtil(FilterUtil<Position> filterUtil) {
        this.filterUtil = filterUtil;
    }
    @Autowired
    public void setTimingConfig(TimingConfig timingConfig) {
        this.timingConfig = timingConfig;
    }

    @Override
    public PositionResponse create(PositionRequest dto) {
        entityValidator.validate(dto);
        return PositionMapper.toResponse(positionRepository.save(PositionMapper.toEntity(dto)));
    }

    @Override
    public PositionResponse get(Long id) {
        return PositionMapper.toResponse(positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Position not found with id: " + id)));
    }

    @Override
    public PositionResponse update(Long id, PositionRequest dto) {
        entityValidator.validate(dto);
        Position existingPosition = PositionMapper.toEntity(dto);
        if (positionRepository.findById(id).isPresent()) {
            existingPosition.setId(id);
        } else throw new EntityNotFoundException("Position not found with id: " + id);
        return PositionMapper.toResponse(positionRepository.save(existingPosition));
    }

    @Override
    public void delete(Long id) {
        if (!positionRepository.existsById(id)) {
            throw new EntityNotFoundException("Position not found with id: " + id);
        }
        positionRepository.deleteById(id);
    }

    @Override
    public List<PositionResponse> getAll() {
        return positionRepository.findAll().stream().map(PositionMapper::toResponse).toList();
    }

    @Override
    public Page<PositionResponse> getPage(int size, int number, String sortBy, String sortDirection,
                                          Map<String, String> filters, Map<String, String> dateRanges) {

        Sort.Direction direction = sortDirection != null ? Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "controlDate");
        Pageable pageable = PageRequest.of(number, size, sort);
        Specification<Position> specification = filterUtil.buildSpecification(filters, dateRanges);
        return positionRepository.findAll(specification, pageable).map(PositionMapper::toResponse);
    }

    @Override
    public List<PositionResponse> createAll(List<PositionRequest> positionRequests) {
        return positionRepository.saveAll(positionRequests.stream().map((e) -> {
                    entityValidator.validate(e);
                    return PositionMapper.toEntity(e);
                }).collect(Collectors.toList()))
                .stream().map(PositionMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ReportStatusResponse> getReportStatuses(String taxpayerNumber) {
        ZoneId zoneId = ZoneId.of(DateTimeConfig.getDefaultValue());
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        return positionRepository.findLatestPositionsByTaxpayerNumber(taxpayerNumber).stream().map((e) -> {
            ReportStatusResponse reportStatusResponse = new ReportStatusResponse();
            reportStatusResponse.setControllerNumber(e.getControllerNumber());
            reportStatusResponse.setLineNumber(e.getLineNumber());
            reportStatusResponse.setLastReportTime(e.getControlDate());
            Duration duration = Duration.between(e.getControlDate(), now);
            if (duration.toMillis() <= timingConfig.getGreenFiveminuteTiming()) {
                reportStatusResponse.setReportStatus(ReportStatus.OK.toString());
            } else if (duration.toMillis() <= timingConfig.getYellowFiveminuteTiming()) {
                reportStatusResponse.setReportStatus(ReportStatus.WARN.toString());
            } else {
                reportStatusResponse.setReportStatus(ReportStatus.ERROR.toString());
            }
            return reportStatusResponse;
        }).collect(Collectors.toList());
    }
}
