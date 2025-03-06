package centrikt.factory_monitoring.five_minute_report.controllers;

import centrikt.factory_monitoring.five_minute_report.dtos.extra.PageRequest;
import centrikt.factory_monitoring.five_minute_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.five_minute_report.enums.Mode;
import centrikt.factory_monitoring.five_minute_report.enums.Status;
import centrikt.factory_monitoring.five_minute_report.models.Position;
import centrikt.factory_monitoring.five_minute_report.services.PositionService;
import centrikt.factory_monitoring.five_minute_report.utils.ftp.FTPUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PositionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PositionService positionService;

    @Mock
    private FTPUtil ftpUtil;

    @InjectMocks
    private PositionController positionController;

    private ObjectMapper objectMapper;

    private PositionRequest positionRequest;
    private Position position;
    private PositionResponse positionResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(positionController).build();
        objectMapper = new ObjectMapper();

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
    }

    @Test
    void getPosition_shouldReturnPosition_whenValidId() throws Exception {

        when(positionService.get(1L)).thenReturn(positionResponse);

        mockMvc.perform(get("/api/v1/five-minute-report/positions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taxpayerNumber").value("123456789012"))
                .andExpect(jsonPath("$.sensorNumber").value("67_03"))
                .andExpect(jsonPath("$.status").value(Status.ACCEPTED_IN_RAR.getDescription()))
                .andExpect(jsonPath("$.mode").value(Mode.SHIPMENT.getDescription()));

        verify(positionService, times(1)).get(1L);
    }

    @Test
    void createPosition_shouldReturnCreatedPosition() throws Exception {

        when(positionService.create(any(PositionRequest.class))).thenReturn(positionResponse);

        mockMvc.perform(post("/api/v1/five-minute-report/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(positionRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taxpayerNumber").value("123456789012"))
                .andExpect(jsonPath("$.sensorNumber").value("67_03"))
                .andExpect(jsonPath("$.status").value(Status.ACCEPTED_IN_RAR.getDescription()))
                .andExpect(jsonPath("$.mode").value(Mode.SHIPMENT.getDescription()));

        verify(positionService, times(1)).create(any(PositionRequest.class));
    }
    @Test
    void updatePosition_shouldReturnUpdatedPositionResponse() throws Exception {

        when(positionService.update(eq(1L), any(PositionRequest.class))).thenReturn(positionResponse);

        mockMvc.perform(put("/api/v1/five-minute-report/positions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(positionRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.taxpayerNumber").value("123456789012"))
                .andExpect(jsonPath("$.sensorNumber").value("67_03"))
                .andExpect(jsonPath("$.status").value(Status.ACCEPTED_IN_RAR.getDescription()))
                .andExpect(jsonPath("$.mode").value(Mode.SHIPMENT.getDescription()));

        verify(positionService, times(1)).update(eq(1L), any(PositionRequest.class));
    }


    @Test
    void deletePosition_shouldReturnNoContent() throws Exception {
        doNothing().when(positionService).delete(1L);

        mockMvc.perform(delete("/api/v1/five-minute-report/positions/1"))
                .andExpect(status().isNoContent());

        verify(positionService, times(1)).delete(1L);
    }

    @Test
    void uploadReport_shouldReturnOk_whenUploadSuccessful() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "report.csv", "text/csv", "data".getBytes());

        when(ftpUtil.saveFileLocally(any(MultipartFile.class))).thenReturn("/tmp/report.csv");
        when(ftpUtil.saveFileToFTP(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(multipart("/api/v1/five-minute-report/positions/upload").file(file))
                .andExpect(status().isOk());

        verify(ftpUtil, times(1)).saveFileLocally(any(MultipartFile.class));
        verify(ftpUtil, times(1)).saveFileToFTP(anyString(), anyString());
    }

    @Test
    void uploadReport_shouldReturnInternalServerError_whenUploadFails() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "report.csv", "text/csv", "data".getBytes());

        when(ftpUtil.saveFileLocally(any(MultipartFile.class))).thenReturn("/tmp/report.csv");
        when(ftpUtil.saveFileToFTP(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(multipart("/api/v1/five-minute-report/positions/upload").file(file).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Failed to upload file"));

        verify(ftpUtil, times(1)).saveFileLocally(any(MultipartFile.class));
        verify(ftpUtil, times(1)).saveFileToFTP(anyString(), anyString());
    }

    @Test
    void getPagePositions_shouldReturnPagedPositions() throws Exception {
        Page<PositionResponse> positionPage = new TestPage<>(Collections.singletonList(positionResponse), org.springframework.data.domain.PageRequest.of(0, 10), 1);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setNumber(0);
        pageRequest.setSize(10);
        pageRequest.setSortBy("taxpayerNumber");
        pageRequest.setSortDirection("ASC");
        pageRequest.setFilters(new HashMap<>());
        pageRequest.setDateRanges(new HashMap<>());

        when(positionService.getPage(anyInt(), anyInt(), any(), any(), any(), any())).thenReturn(positionPage);

        mockMvc.perform(post("/api/v1/five-minute-report/positions/fetch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].taxpayerNumber").value("123456789012"))
                .andExpect(jsonPath("$.content[0].sensorNumber").value("67_03"))
                .andExpect(jsonPath("$.content[0].status").value(Status.ACCEPTED_IN_RAR.getDescription()))
                .andExpect(jsonPath("$.content[0].mode").value(Mode.SHIPMENT.getDescription()));

        verify(positionService, times(1)).getPage(anyInt(), anyInt(), any(), any(), any(), any());
    }

    @Test
    void getPagePositionsByTaxpayerNumber_shouldReturnPagedPositions() throws Exception {
        Page<PositionResponse> positionPage = new TestPage<>(Collections.singletonList(positionResponse), org.springframework.data.domain.PageRequest.of(0, 10), 1);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setNumber(0);
        pageRequest.setSize(10);
        pageRequest.setSortBy("taxpayerNumber");
        pageRequest.setSortDirection("ASC");
        pageRequest.setFilters(new HashMap<>());
        pageRequest.setDateRanges(new HashMap<>());

        when(positionService.getPage(anyInt(), anyInt(), any(), any(), any(), any())).thenReturn(positionPage);

        mockMvc.perform(post("/api/v1/five-minute-report/positions/fetch/" + positionRequest.getTaxpayerNumber())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].taxpayerNumber").value("123456789012"))
                .andExpect(jsonPath("$.content[0].sensorNumber").value("67_03"))
                .andExpect(jsonPath("$.content[0].status").value(Status.ACCEPTED_IN_RAR.getDescription()))
                .andExpect(jsonPath("$.content[0].mode").value(Mode.SHIPMENT.getDescription()));

        verify(positionService, times(1)).getPage(anyInt(), anyInt(), any(), any(), any(), any());
    }

    private class TestPage<T> extends PageImpl<T> {

        @JsonIgnore
        private Pageable pageable;

        TestPage(List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
            this.pageable = pageable;
        }
    }

}
