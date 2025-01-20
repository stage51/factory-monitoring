package centrikt.factorymonitoring.modereport;

import centrikt.factorymonitoring.modereport.dtos.extra.PageRequestDTO;
import centrikt.factorymonitoring.modereport.dtos.requests.PositionRequest;
import centrikt.factorymonitoring.modereport.dtos.requests.ProductRequest;
import centrikt.factorymonitoring.modereport.dtos.responses.PositionResponse;
import centrikt.factorymonitoring.modereport.dtos.responses.ProductResponse;
import centrikt.factorymonitoring.modereport.utils.jwt.JwtTokenUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
		"spring.config.location=classpath:/application-test.yml"
})
class ModeReportApplicationTests {

	@LocalServerPort
	private int port;

	@Container
	public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11-alpine")
			.withDatabaseName("mode-report")
			.withUsername("admin")
			.withPassword("admin");

	@Container
	public static GenericContainer rabbitMQContainer = new GenericContainer<>(DockerImageName.parse("rabbitmq:management"))
			.withExposedPorts(5672, 15672)
			.withEnv("RABBITMQ_DEFAULT_USER", "admin")
			.withEnv("RABBITMQ_DEFAULT_PASS", "admin");

	@DynamicPropertySource
	static void postgresqlProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
	}

	@DynamicPropertySource
	static void rabbitMqProperties(DynamicPropertyRegistry registry) {
		Integer amqpPort = rabbitMQContainer.getMappedPort(5672);
		Integer managementPort = rabbitMQContainer.getMappedPort(15672);

		registry.add("spring.rabbitmq.host", () -> "localhost");
		registry.add("spring.rabbitmq.port", () -> amqpPort);
		registry.add("spring.rabbitmq." +
				"username", () -> "admin");
		registry.add("spring.rabbitmq.password", () -> "admin");
		registry.add("spring.rabbitmq.virtual-host", () -> "/");
	}

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	private String getValidJwtToken() {
		return jwtTokenUtil.generateTokenForTest("TEST_USER", "ROLE_ADMIN");
	}

	private HttpHeaders createAuthorizationHeaders() {
		String jwtToken = getValidJwtToken();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + jwtToken);
		return headers;
	}

	@Test
	void contextLoads() {
		Assertions.assertTrue(postgreSQLContainer.isRunning(), "Postgres container is not running");
		Assertions.assertTrue(rabbitMQContainer.isRunning(), "RabbitMQ container is not running");
		Assertions.assertNotNull(rabbitMQContainer.getFirstMappedPort(), "RabbitMQ port is not mapped");
	}

	@Nested
	class PositionControllerTests {
		@Test
		void testPositionCRUDOperations() {
			HttpHeaders headers = createAuthorizationHeaders();
			testCreatePosition(headers);
			testGetPosition(headers);
			testUpdatePosition(headers);
			testDeletePosition(headers);
		}

		void testCreatePosition(HttpHeaders headers) {
			PositionRequest positionRequest = new PositionRequest();

			positionRequest.setTaxpayerNumber("123456789012");
			positionRequest.setSensorNumber("12_34");

			ZonedDateTime now = ZonedDateTime.now();
			positionRequest.setStartDate(now);
			positionRequest.setEndDate(now.plusHours(1));

			positionRequest.setVbsStart(new BigDecimal("1000.00"));
			positionRequest.setVbsEnd(new BigDecimal("1500.00"));
			positionRequest.setAStart(new BigDecimal("500.00"));
			positionRequest.setAEnd(new BigDecimal("600.00"));
			positionRequest.setPercentAlc(new BigDecimal("96.5"));
			positionRequest.setBottleCountStart(new BigDecimal("1000"));
			positionRequest.setBottleCountEnd(new BigDecimal("1200"));
			positionRequest.setTemperature(new BigDecimal("25.5"));

			positionRequest.setMode("Производство продукции");
			positionRequest.setStatus("Принято в РАР");

			HttpEntity<PositionRequest> entity = new HttpEntity<>(positionRequest, headers);
			ResponseEntity<PositionResponse> response = restTemplate.exchange(
					"/api/v1/mode-report/positions",
					HttpMethod.POST,
					entity,
					PositionResponse.class
			);

			Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
			Assertions.assertNotNull(response.getBody());
			Assertions.assertNotNull(response.getBody().getId());
		}

		void testGetPosition(HttpHeaders headers) {
			Long id = 1L;

			HttpEntity<Void> entity = new HttpEntity<>(headers);

			ResponseEntity<PositionResponse> response = restTemplate.exchange(
					"/api/v1/mode-report/positions/{id}",
					HttpMethod.GET,
					entity,
					PositionResponse.class,
					id
			);

			Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
			Assertions.assertNotNull(response.getBody());
			Assertions.assertEquals(id, response.getBody().getId());
		}

		void testUpdatePosition(HttpHeaders headers) {
			Long id = 1L;

			PositionRequest positionRequest = new PositionRequest();

			positionRequest.setTaxpayerNumber("123456789013");
			positionRequest.setSensorNumber("12_55");

			ZonedDateTime now = ZonedDateTime.now();
			positionRequest.setStartDate(now);
			positionRequest.setEndDate(now.plusHours(1));

			positionRequest.setVbsStart(new BigDecimal("1000.00"));
			positionRequest.setVbsEnd(new BigDecimal("1500.00"));
			positionRequest.setAStart(new BigDecimal("500.00"));
			positionRequest.setAEnd(new BigDecimal("600.00"));
			positionRequest.setPercentAlc(new BigDecimal("96.5"));
			positionRequest.setBottleCountStart(new BigDecimal("1000"));
			positionRequest.setBottleCountEnd(new BigDecimal("1200"));
			positionRequest.setTemperature(new BigDecimal("25.5"));

			positionRequest.setMode("Производство продукции");
			positionRequest.setStatus("Принято в РАР");

			HttpEntity<PositionRequest> entity = new HttpEntity<>(positionRequest, headers);

			ResponseEntity<PositionResponse> response = restTemplate.exchange(
					"/api/v1/mode-report/positions/{id}",
					HttpMethod.PUT,
					entity,
					PositionResponse.class,
					id
			);

			Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
			Assertions.assertNotNull(response.getBody());
			Assertions.assertEquals(id, response.getBody().getId());
			Assertions.assertEquals(positionRequest.getTaxpayerNumber(), response.getBody().getTaxpayerNumber());
			Assertions.assertEquals(positionRequest.getSensorNumber(), response.getBody().getSensorNumber());
		}

		void testDeletePosition(HttpHeaders headers) {
			Long id = 1L;


			HttpEntity<PositionRequest> entity = new HttpEntity<>(headers);

			ResponseEntity<Void> deleteResponse = restTemplate.exchange(
					"/api/v1/mode-report/positions/{id}",
					HttpMethod.DELETE,
					entity,
					Void.class,
					id
			);

			ResponseEntity<PositionResponse> getResponse = restTemplate.exchange(
					"/api/v1/mode-report/positions/{id}",
					HttpMethod.GET,
					entity,
					PositionResponse.class,
					id
			);

			Assertions.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
			Assertions.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
		}

		@Test
		void testUploadFile() throws Exception {
			MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", file.getResource());

			HttpHeaders headers = createAuthorizationHeaders();

			HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

			String baseUrl = "http://localhost:" + port;

			ResponseEntity<String> response = restTemplate.exchange(
					baseUrl + "/api/v1/mode-report/positions/upload",
					HttpMethod.POST,
					entity,
					String.class
			);

			Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		}

		@Test
		void TestPatchCreationAndPagesFetching() {
			HttpHeaders headers = createAuthorizationHeaders();
			testPatchCreation(headers);
			testGetPagePositions(headers);
		}

		void testPatchCreation(HttpHeaders headers) {

			List<PositionRequest> list = new ArrayList<>();

			for (int i = 0; i < 9; i++) {
				PositionRequest positionRequest = new PositionRequest();

				positionRequest.setTaxpayerNumber("12345678901" + i);
				positionRequest.setSensorNumber("12_3" + i);

				ZonedDateTime now = ZonedDateTime.now();
				positionRequest.setStartDate(now);
				positionRequest.setEndDate(now.plusHours(1));

				positionRequest.setVbsStart(new BigDecimal("1000.00"));
				positionRequest.setVbsEnd(new BigDecimal("1500.00"));
				positionRequest.setAStart(new BigDecimal("500.00"));
				positionRequest.setAEnd(new BigDecimal("600.00"));
				positionRequest.setPercentAlc(new BigDecimal("96.5"));
				positionRequest.setBottleCountStart(new BigDecimal("1000"));
				positionRequest.setBottleCountEnd(new BigDecimal("1200"));
				positionRequest.setTemperature(new BigDecimal("25.5"));

				positionRequest.setMode("Производство продукции");
				positionRequest.setStatus("Принято в РАР");

				list.add(positionRequest);
			}

			HttpEntity<List<PositionRequest>> entity = new HttpEntity<>(list, headers);

			ResponseEntity<List<PositionResponse>> response = restTemplate.exchange(
					"/api/v1/mode-report/positions/list",
					HttpMethod.POST,
					entity,
					new ParameterizedTypeReference<>() {}
			);
			List<PositionResponse> positionResponses = response.getBody();

			Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
			Assertions.assertNotNull(response.getBody());
			Assertions.assertEquals(9, positionResponses.size());
			Assertions.assertEquals(1, positionResponses.get(0).getId());
			Assertions.assertEquals(9, positionResponses.get(8).getId());
		}

		void testGetPagePositions(HttpHeaders headers) {
			PageRequestDTO pageRequestDTO = new PageRequestDTO();
			pageRequestDTO.setSize(10);
			pageRequestDTO.setNumber(0);
			pageRequestDTO.setSortBy("id");
			pageRequestDTO.setSortDirection("ASC");

			pageRequestDTO.setFilters(new HashMap<>());
			pageRequestDTO.setDateRanges(new HashMap<>());

			HttpEntity<PageRequestDTO> entity = new HttpEntity<>(pageRequestDTO, headers);

			ResponseEntity<TestPage<PositionResponse>> responseWithoutTaxpayer = restTemplate.exchange(
					"/api/v1/mode-report/positions/fetch",
					HttpMethod.POST,
					entity,
					new ParameterizedTypeReference<TestPage<PositionResponse>>() {}
			);

			Assertions.assertEquals(HttpStatus.OK, responseWithoutTaxpayer.getStatusCode());
			Assertions.assertNotNull(responseWithoutTaxpayer.getBody());
			Assertions.assertFalse(responseWithoutTaxpayer.getBody().getContent().isEmpty());

			String taxpayerNumber = "123456789012";
			ResponseEntity<TestPage<PositionResponse>> responseWithTaxpayer = restTemplate.exchange(
					"/api/v1/mode-report/positions/fetch/{taxpayerNumber}",
					HttpMethod.POST,
					entity,
					new ParameterizedTypeReference<TestPage<PositionResponse>>() {},
					taxpayerNumber
			);

			Assertions.assertEquals(HttpStatus.OK, responseWithTaxpayer.getStatusCode());
			Assertions.assertNotNull(responseWithTaxpayer.getBody());
			Assertions.assertFalse(responseWithTaxpayer.getBody().getContent().isEmpty());

			List<PositionResponse> content = responseWithTaxpayer.getBody().getContent();
			for (PositionResponse position : content) {
				Assertions.assertEquals(taxpayerNumber, position.getTaxpayerNumber());
			}
		}
	}

    @Nested
    class ProductControllerTests {

		@Test
		void testCreateProduct() {
			HttpHeaders headers = createAuthorizationHeaders();
			ProductRequest productRequest = new ProductRequest();
			productRequest.setUnitType("Фасованная");
			productRequest.setFullName("Вино Красное");
			productRequest.setAlcCode("123456");
			productRequest.setProductVCode("A123");

			HttpEntity<ProductRequest> entity = new HttpEntity<>(productRequest, headers);
			ResponseEntity<ProductResponse> response = restTemplate.exchange(
					"/api/v1/mode-report/products",
					HttpMethod.POST,
					entity,
					ProductResponse.class
			);

			Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
			Assertions.assertNotNull(response.getBody());
			Assertions.assertNotNull(response.getBody().getId());
			Assertions.assertEquals(productRequest.getFullName(), response.getBody().getFullName());
		}

		@Test
		void testGetProductById() {
			HttpHeaders headers = createAuthorizationHeaders();
			headers.add("Accept", "application/json");
			headers.add("Content-Type", "application/json");
			Long productId = 1L;

			HttpEntity<Void> entity = new HttpEntity<>(headers);
			ResponseEntity<ProductResponse> response = restTemplate.exchange(
					"/api/v1/mode-report/products/{id}",
					HttpMethod.GET,
					entity,
					ProductResponse.class,
					productId
			);

			Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
			Assertions.assertNotNull(response.getBody());
			Assertions.assertEquals(productId, response.getBody().getId());
		}

		@Test
		void testUpdateProduct() {
			HttpHeaders headers = createAuthorizationHeaders();
			Long productId = 1L;

			ProductRequest productRequest = new ProductRequest();
			productRequest.setUnitType("Нефасованная");
			productRequest.setFullName("Вино Белое");
			productRequest.setAlcCode("654321");
			productRequest.setProductVCode("B456");

			HttpEntity<ProductRequest> entity = new HttpEntity<>(productRequest, headers);
			ResponseEntity<ProductResponse> response = restTemplate.exchange(
					"/api/v1/mode-report/products/{id}",
					HttpMethod.PUT,
					entity,
					ProductResponse.class,
					productId
			);

			Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
			Assertions.assertNotNull(response.getBody());
			Assertions.assertEquals(productRequest.getFullName(), response.getBody().getFullName());
			Assertions.assertEquals(productId, response.getBody().getId());
		}

		@Test
		void testDeleteProduct() {
			HttpHeaders headers = createAuthorizationHeaders();
			headers.add("Accept", "application/json");
			headers.add("Content-Type", "application/json");
			Long productId = 1L;

			HttpEntity<Void> entity = new HttpEntity<>(headers);
			ResponseEntity<Void> deleteResponse = restTemplate.exchange(
					"/api/v1/mode-report/products/{id}",
					HttpMethod.DELETE,
					entity,
					Void.class,
					productId
			);

			Assertions.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

			ResponseEntity<ProductResponse> getResponse = restTemplate.exchange(
					"/api/v1/mode-report/products/{id}",
					HttpMethod.GET,
					entity,
					ProductResponse.class,
					productId
			);

			Assertions.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
		}

		@Test
		void testFetchPageProducts() {
			HttpHeaders headers = createAuthorizationHeaders();

			PageRequestDTO pageRequestDTO = new PageRequestDTO();
			pageRequestDTO.setSize(10);
			pageRequestDTO.setNumber(0);
			pageRequestDTO.setSortBy("id");
			pageRequestDTO.setSortDirection("ASC");

			HttpEntity<PageRequestDTO> entity = new HttpEntity<>(pageRequestDTO, headers);

			ResponseEntity<TestPage<ProductResponse>> response = restTemplate.exchange(
					"/api/v1/mode-report/products/fetch",
					HttpMethod.POST,
					entity,
					new ParameterizedTypeReference<TestPage<ProductResponse>>() {}
			);

			Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
			Assertions.assertNotNull(response.getBody());
			Assertions.assertFalse(response.getBody().getContent().isEmpty());
		}
	}

	private static class TestPage<T> extends PageImpl<T> {
		@JsonIgnore
		private Pageable pageable;

		TestPage(List<T> content, Pageable pageable, long total) {
			super(content, pageable, total);
			this.pageable = pageable;
		}

		public TestPage() {
			super(new ArrayList<>(), Pageable.unpaged(), 0);
		}
	}
}

