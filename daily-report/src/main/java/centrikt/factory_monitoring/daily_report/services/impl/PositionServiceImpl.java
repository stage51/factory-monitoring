package centrikt.factory_monitoring.daily_report.services.impl;

import centrikt.factory_monitoring.daily_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.daily_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.daily_report.exceptions.EntityNotFoundException;
import centrikt.factory_monitoring.daily_report.mappers.PositionMapper;
import centrikt.factory_monitoring.daily_report.models.Position;
import centrikt.factory_monitoring.daily_report.repos.PositionRepository;
import centrikt.factory_monitoring.daily_report.services.PositionService;
import centrikt.factory_monitoring.daily_report.utils.filter.FilterUtil;
import centrikt.factory_monitoring.daily_report.utils.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PositionServiceImpl implements PositionService {

    private PositionRepository positionRepository;
    private EntityValidator entityValidator;
    private FilterUtil<Position> filterUtil;

    public PositionServiceImpl(PositionRepository positionRepository, EntityValidator entityValidator,
                               FilterUtil<Position> filterUtil) {
        this.positionRepository = positionRepository;
        this.entityValidator = entityValidator;
        this.filterUtil = filterUtil;
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
        if (positionRepository.findById(id).isPresent()){
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
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "startDate");
        Pageable pageable = PageRequest.of(number, size, sort);
        Specification<Position> specification = filterUtil.buildSpecification(filters, dateRanges);
        return positionRepository.findAll(specification, pageable).map(PositionMapper::toResponse);
    }
}