package centrikt.factorymonitoring.modereport.controllers;

import centrikt.factorymonitoring.modereport.dtos.extra.PageRequest;
import centrikt.factorymonitoring.modereport.dtos.requests.ProductRequest;
import centrikt.factorymonitoring.modereport.dtos.responses.ProductResponse;
import centrikt.factorymonitoring.modereport.enums.UnitType;
import centrikt.factorymonitoring.modereport.services.ProductService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private ProductRequest productRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();

        productRequest = new ProductRequest();
        productRequest.setFullName("Product Name");
        productRequest.setAlcCode("12345");
        productRequest.setProductVCode("54321");
        productRequest.setUnitType(UnitType.PACKED.getUnitType());

        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setFullName("Product Name");
        productResponse.setAlcCode("12345");
        productResponse.setProductVCode("54321");
        productResponse.setUnitType(UnitType.PACKED.getUnitType());
    }

    @Test
    void getProduct_shouldReturnProductResponse_whenValidId() throws Exception {
        when(productService.get(1L)).thenReturn(productResponse);

        mockMvc.perform(get("/api/v1/mode-report/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Product Name"))
                .andExpect(jsonPath("$.alcCode").value("12345"))
                .andExpect(jsonPath("$.productVCode").value("54321"))
                .andExpect(jsonPath("$.unitType").value(UnitType.PACKED.getUnitType()));

        verify(productService, times(1)).get(1L);
    }

    @Test
    void getPagePositions_shouldReturnPagedProducts() throws Exception {
        Page<ProductResponse> productPage = new TestPage<>(Collections.singletonList(productResponse), org.springframework.data.domain.PageRequest.of(0, 10), 1);

        PageRequest pageRequest = new PageRequest();
        pageRequest.setNumber(0);
        pageRequest.setSize(10);
        pageRequest.setSortBy("fullName");
        pageRequest.setSortDirection("ASC");
        pageRequest.setFilters(new HashMap<>());
        pageRequest.setDateRanges(new HashMap<>());

        when(productService.getPage(anyInt(), anyInt(), any(), any(), any(), any())).thenReturn(productPage);

        mockMvc.perform(post("/api/v1/mode-report/products/fetch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].fullName").value("Product Name"));

        verify(productService, times(1)).getPage(anyInt(), anyInt(), any(), any(), any(), any());
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

