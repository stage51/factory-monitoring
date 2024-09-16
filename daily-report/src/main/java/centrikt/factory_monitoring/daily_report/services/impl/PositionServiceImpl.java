package centrikt.factory_monitoring.daily_report.services.impl;

import centrikt.factory_monitoring.daily_report.models.Position;
import centrikt.factory_monitoring.daily_report.repos.PositionRepository;
import centrikt.factory_monitoring.daily_report.services.PositionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionServiceImpl implements PositionService {

    private PositionRepository positionRepository;

    public PositionServiceImpl(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Autowired
    public void setPositionRepository(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Override
    public Position create(Position dto) {
        return positionRepository.save(dto);
    }

    @Override
    public Position get(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Position not found with id: " + id));
    }

    @Override
    public Position update(Long id, Position dto) {
        Position existingPosition = positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Position not found with id: " + id));
        return positionRepository.save(dto);
    }

    @Override
    public void delete(Long id) {
        if (!positionRepository.existsById(id)) {
            throw new EntityNotFoundException("Position not found with id: " + id);
        }
        positionRepository.deleteById(id);
    }

    @Override
    public List<Position> getAll() {
        return positionRepository.findAll();
    }
}
