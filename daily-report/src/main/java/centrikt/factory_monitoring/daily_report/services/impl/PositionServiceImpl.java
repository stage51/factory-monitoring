package centrikt.factory_monitoring.daily_report.services.impl;

import centrikt.factory_monitoring.daily_report.dtos.PositionDTO;
import centrikt.factory_monitoring.daily_report.exceptions.EntityNotFoundException;
import centrikt.factory_monitoring.daily_report.mappers.PositionMapper;
import centrikt.factory_monitoring.daily_report.models.Position;
import centrikt.factory_monitoring.daily_report.repos.PositionRepository;
import centrikt.factory_monitoring.daily_report.services.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionServiceImpl implements PositionService {

    private PositionRepository positionRepository;
    private PositionMapper positionMapper;

    public PositionServiceImpl(PositionRepository positionRepository, PositionMapper positionMapper) {
        this.positionRepository = positionRepository;
        this.positionMapper = positionMapper;
    }

    @Autowired
    public void setPositionRepository(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }
    @Autowired
    public void setPositionMapper(PositionMapper positionMapper) {
        this.positionMapper = positionMapper;
    }

    @Override
    public PositionDTO create(PositionDTO dto) {
        return positionMapper.toDTO(positionRepository.save(positionMapper.toEntity(dto)));
    }

    @Override
    public PositionDTO get(Long id) {
        return positionMapper.toDTO(positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Position not found with id: " + id)));
    }

    @Override
    public PositionDTO update(Long id, PositionDTO dto) {
        Position existingPosition = positionMapper.toEntity(dto);
        if (positionRepository.findById(id).isPresent()){
            existingPosition.setId(id);
        } else throw new EntityNotFoundException("Position not found with id: " + id);
        return positionMapper.toDTO(positionRepository.save(existingPosition));
    }

    @Override
    public void delete(Long id) {
        if (!positionRepository.existsById(id)) {
            throw new EntityNotFoundException("Position not found with id: " + id);
        }
        positionRepository.deleteById(id);
    }

    @Override
    public List<PositionDTO> getAll() {
        return positionRepository.findAll().stream().map(positionMapper::toDTO).toList();
    }
}