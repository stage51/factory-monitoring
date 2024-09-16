package centrikt.factory_monitoring.daily_report.controllers;

import centrikt.factory_monitoring.daily_report.models.Position;
import centrikt.factory_monitoring.daily_report.models.Product;
import centrikt.factory_monitoring.daily_report.services.PositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/daily-report/positions")
@Slf4j
public class PositionController {
    private PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }
    @Autowired
    public void setPositionService(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Position> getPosition(@PathVariable Long id) {
        log.info("Fetching position with id: {}", id);
        Position position = positionService.get(id);
        return ResponseEntity.ok(position);
    }

    @PostMapping()
    public ResponseEntity<Position> createPosition(@RequestBody Position position) {
        log.info("Creating new position: {}", position);
        Position createdPosition = positionService.create(position);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPosition);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Position> updatePosition(@PathVariable Long id, @RequestBody Position position) {
        log.info("Updating position with id: {}", id);
        Position updatedPosition = positionService.update(id, position);
        return ResponseEntity.ok(updatedPosition);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        log.info("Deleting position with id: {}", id);
        positionService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping()
    public ResponseEntity<List<Position>> getAllPositions() {
        log.info("Fetching all positions");
        List<Position> positions = positionService.getAll();
        return ResponseEntity.ok(positions);
    }
}
