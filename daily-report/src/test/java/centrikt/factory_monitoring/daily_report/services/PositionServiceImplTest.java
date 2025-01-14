package centrikt.factory_monitoring.daily_report.services;

import centrikt.factory_monitoring.daily_report.dtos.messages.ReportMessage;
import centrikt.factory_monitoring.daily_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.daily_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.daily_report.enums.Mode;
import centrikt.factory_monitoring.daily_report.enums.Status;
import centrikt.factory_monitoring.daily_report.exceptions.EntityNotFoundException;
import centrikt.factory_monitoring.daily_report.models.Position;
import centrikt.factory_monitoring.daily_report.repos.PositionRepository;
import centrikt.factory_monitoring.daily_report.services.impl.PositionServiceImpl;
import centrikt.factory_monitoring.daily_report.utils.filter.FilterUtil;
import centrikt.factory_monitoring.daily_report.utils.validator.EntityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceImplTest {

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private EntityValidator entityValidator;

    @Mock
    private FilterUtil<Position> filterUtil;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PositionServiceImpl positionService;

    private PositionRequest positionRequest;
    private Position position;
    private PositionResponse positionResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        positionRequest = new PositionRequest();
        positionRequest.setTaxpayerNumber("123456789012");
        positionRequest.setSensorNumber("67_03");
        positionRequest.setStatus(Status.ACCEPTED_IN_RAR.getStatus());
        positionRequest.setMode(Mode.ACCEPTANCE_RETURN.getMode());

        position = new Position();
        position.setId(1L);
        position.setTaxpayerNumber("123456789012");
        position.setLineNumber("67");
        position.setControllerNumber("03");
        position.setStatus(Status.ACCEPTED_IN_RAR);
        position.setMode(Mode.ACCEPTANCE_RETURN);

        positionResponse = new PositionResponse();
        positionResponse.setId(1L);
        positionResponse.setTaxpayerNumber("123456789012");
        positionResponse.setSensorNumber("67_03");
        positionResponse.setStatus(Status.ACCEPTED_IN_RAR.getStatus());
        positionResponse.setMode(Mode.ACCEPTANCE_RETURN.getMode());
    }

    @Test
    void create_ShouldSavePositionAndSendMessage() {
        when(positionRepository.save(any(Position.class))).thenReturn(position);
        doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(ReportMessage.class));

        PositionResponse response = positionService.create(positionRequest);

        verify(entityValidator, times(1)).validate(positionRequest);
        verify(rabbitTemplate, times(1)).convertAndSend(eq("reportQueue"), any(ReportMessage.class));
        verify(positionRepository, times(1)).save(any(Position.class));

        assertNotNull(response);
        assertEquals(positionResponse.getId(), response.getId());
    }

    @Test
    void get_ShouldThrowException_WhenPositionNotFound() {
        when(positionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> positionService.get(1L));
    }

    @Test
    void update_ShouldUpdatePosition_WhenPositionExists() {
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        when(positionRepository.save(any(Position.class))).thenReturn(position);

        PositionResponse response = positionService.update(1L, positionRequest);

        verify(entityValidator, times(1)).validate(positionRequest);
        verify(positionRepository, times(1)).save(any(Position.class));

        assertNotNull(response);
        assertEquals(positionResponse.getId(), response.getId());
    }

    @Test
    void update_ShouldThrowException_WhenPositionNotFound() {
        when(positionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> positionService.update(1L, positionRequest));
    }

    @Test
    void delete_ShouldDeletePosition_WhenPositionExists() {
        when(positionRepository.existsById(1L)).thenReturn(true);

        positionService.delete(1L);

        verify(positionRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenPositionNotFound() {
        when(positionRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> positionService.delete(1L));
    }

    @Test
    void getAll_ShouldReturnAllPositions() {
        when(positionRepository.findAll()).thenReturn(Collections.singletonList(position));

        List<PositionResponse> responses = positionService.getAll();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(positionResponse.getId(), responses.get(0).getId());
    }

    @Test
    void getPage_ShouldReturnPagedPositions() {
        Page<Position> positionPage = new PageImpl<>(Collections.singletonList(position));
        when(filterUtil.buildSpecification(any(Map.class), any(Map.class))).thenReturn((Specification<Position>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), position.getStatus()));
        when(positionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(positionPage);

        Page<PositionResponse> responses = positionService.getPage(10, 0, "id", "ASC", new HashMap<>(), new HashMap<>());

        assertNotNull(responses);
        assertEquals(1, responses.getContent().size());
        assertEquals(positionResponse.getId(), responses.getContent().get(0).getId());
    }
}
