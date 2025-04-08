package centrikt.factory_monitoring.daily_report.services;

import centrikt.factory_monitoring.daily_report.dtos.messages.ReportMessage;
import centrikt.factory_monitoring.daily_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.daily_report.dtos.requests.ProductRequest;
import centrikt.factory_monitoring.daily_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.daily_report.dtos.responses.ProductResponse;
import centrikt.factory_monitoring.daily_report.enums.Mode;
import centrikt.factory_monitoring.daily_report.enums.Status;
import centrikt.factory_monitoring.daily_report.enums.Type;
import centrikt.factory_monitoring.daily_report.enums.UnitType;
import centrikt.factory_monitoring.daily_report.exceptions.EntityNotFoundException;
import centrikt.factory_monitoring.daily_report.models.Position;
import centrikt.factory_monitoring.daily_report.models.Product;
import centrikt.factory_monitoring.daily_report.repos.PositionRepository;
import centrikt.factory_monitoring.daily_report.repos.ProductRepository;
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
    private ProductRepository productRepository;

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

    private ProductRequest productRequest;
    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        positionRequest = new PositionRequest();
        positionRequest.setTaxpayerNumber("123456789012");
        positionRequest.setSensorNumber("67_03");
        positionRequest.setStatus(Status.ACCEPTED_IN_RAR.getDescription());
        positionRequest.setMode(Mode.SHIPMENT.getCode());

        position = new Position();
        position.setId(1L);
        position.setTaxpayerNumber("123456789012");
        position.setLineNumber("67");
        position.setControllerNumber("03");
        position.setStatus(Status.ACCEPTED_IN_RAR);
        position.setMode(Mode.SHIPMENT);

        positionResponse = new PositionResponse();
        positionResponse.setId(1L);
        positionResponse.setTaxpayerNumber("123456789012");
        positionResponse.setSensorNumber("67_03");
        positionResponse.setStatus(Status.ACCEPTED_IN_RAR.getDescription());
        positionResponse.setMode(Mode.SHIPMENT.getDescription());

        productRequest = new ProductRequest();
        productRequest.setFullName("Product Name");
        productRequest.setAlcCode("12345");
        productRequest.setProductVCode("54321");
        productRequest.setUnitType(UnitType.PACKED.getUnitType());
        productRequest.setType(Type.ALCOHOL.getType());

        product = new Product();
        product.setId(1L);
        product.setFullName("Product Name");
        product.setAlcCode("12345");
        product.setProductVCode("54321");
        product.setUnitType(UnitType.PACKED);
        product.setType(Type.ALCOHOL);

        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setFullName("Product Name");
        productResponse.setAlcCode("12345");
        productResponse.setProductVCode("54321");
        productResponse.setUnitType(UnitType.PACKED.getUnitType());
        productResponse.setType(Type.ALCOHOL.getType());

        positionRequest.setProduct(productRequest);
        position.setProduct(product);
        positionResponse.setProduct(productResponse);
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
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
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
