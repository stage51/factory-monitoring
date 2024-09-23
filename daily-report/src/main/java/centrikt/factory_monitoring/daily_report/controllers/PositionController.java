package centrikt.factory_monitoring.daily_report.controllers;

import centrikt.factory_monitoring.daily_report.dtos.PositionDTO;
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

    @GetMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PositionDTO> getPosition(@PathVariable Long id) {
        log.info("Fetching position with id: {}", id);
        PositionDTO position = positionService.get(id);
        return ResponseEntity.ok(position);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PositionDTO> createPosition(@RequestBody PositionDTO positionDTO) {
        log.info("Creating new position: {}", positionDTO);
        PositionDTO createdPosition = positionService.create(positionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPosition);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PositionDTO> updatePosition(@PathVariable Long id, @RequestBody PositionDTO positionDTO) {
        log.info("Updating position with id: {}", id);
        PositionDTO updatedPosition = positionService.update(id, positionDTO);
        return ResponseEntity.ok(updatedPosition);
    }

    @DeleteMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        log.info("Deleting position with id: {}", id);
        positionService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<PositionDTO>> getAllPositions() {
        log.info("Fetching all positions");
        List<PositionDTO> positions = positionService.getAll();
        return ResponseEntity.ok(positions);
    }
}
