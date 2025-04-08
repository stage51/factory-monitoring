package centrikt.factorymonitoring.modereport;

import centrikt.factorymonitoring.modereport.dtos.extra.PageRequest;
import centrikt.factorymonitoring.modereport.dtos.requests.PositionRequest;
import centrikt.factorymonitoring.modereport.dtos.requests.ProductRequest;
import centrikt.factorymonitoring.modereport.dtos.responses.PositionResponse;
import centrikt.factorymonitoring.modereport.dtos.responses.ProductResponse;
import centrikt.factorymonitoring.modereport.enums.UnitType;
import centrikt.factorymonitoring.modereport.utils.jwt.JwtTokenUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
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
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class PositionProductIntegrationTests {

		private HttpHeaders headers;
		private PositionRequest positionRequest;
		private ProductRequest productRequest;

		@BeforeEach
		void setup() {
			headers = createAuthorizationHeaders();

			productRequest = new ProductRequest();
			productRequest.setUnitType("Фасованная");
			productRequest.setFullName("Водка");
			productRequest.setAlcCode("123456");
			productRequest.setProductVCode("A123");

			positionRequest = new PositionRequest();
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
			positionRequest.setMode("009");
			positionRequest.setStatus("Принято в РАР");
			positionRequest.setProduct(productRequest);
		}

		@Test
		void testCRUDAndProductRemovalWithPosition() {
			// CREATE
			HttpEntity<PositionRequest> createEntity = new HttpEntity<>(positionRequest, headers);
			ResponseEntity<PositionResponse> createResponse = restTemplate.exchange(
					"/api/v1/mode-report/positions",
					HttpMethod.POST,
					createEntity,
					PositionResponse.class
			);
			Assertions.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
			Long positionId = createResponse.getBody().getId();
			Long productId = createResponse.getBody().getProduct().getId();

			// READ position
			ResponseEntity<PositionResponse> getResponse = restTemplate.exchange(
					"/api/v1/mode-report/positions/{id}",
					HttpMethod.GET,
					new HttpEntity<>(headers),
					PositionResponse.class,
					positionId
			);
			Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
			Assertions.assertEquals(positionId, getResponse.getBody().getId());

			// READ product
			ResponseEntity<ProductResponse> productResponse = restTemplate.exchange(
					"/api/v1/mode-report/products/{id}",
					HttpMethod.GET,
					new HttpEntity<>(headers),
					ProductResponse.class,
					productId
			);
			Assertions.assertEquals(HttpStatus.OK, productResponse.getStatusCode());
			Assertions.assertEquals(productRequest.getFullName(), productResponse.getBody().getFullName());

			// UPDATE position
			positionRequest.setSensorNumber("123_123");
			productRequest.setAlcCode("654321");
			positionRequest.setProduct(productRequest);
			HttpEntity<PositionRequest> updateEntity = new HttpEntity<>(positionRequest, headers);
			ResponseEntity<PositionResponse> updatePositionResponse = restTemplate.exchange(
					"/api/v1/mode-report/positions/{id}",
					HttpMethod.PUT,
					updateEntity,
					PositionResponse.class,
					positionId
			);
			Assertions.assertEquals(HttpStatus.OK, updatePositionResponse.getStatusCode());
			Assertions.assertEquals("123_123", updatePositionResponse.getBody().getSensorNumber());
			Assertions.assertEquals("654321", updatePositionResponse.getBody().getProduct().getAlcCode());

			ResponseEntity<ProductResponse> updateProductResponse = restTemplate.exchange(
					"/api/v1/mode-report/products/{id}",
					HttpMethod.GET,
					new HttpEntity<>(headers),
					ProductResponse.class,
					productId
			);
			Assertions.assertEquals(HttpStatus.OK, updateProductResponse.getStatusCode());
			Assertions.assertEquals("654321", updatePositionResponse.getBody().getProduct().getAlcCode());

			// DELETE position
			ResponseEntity<Void> deleteResponse = restTemplate.exchange(
					"/api/v1/mode-report/positions/{id}",
					HttpMethod.DELETE,
					new HttpEntity<>(headers),
					Void.class,
					positionId
			);
			Assertions.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

			// CHECK position is gone
			ResponseEntity<PositionResponse> deletedPositionCheck = restTemplate.exchange(
					"/api/v1/mode-report/positions/{id}",
					HttpMethod.GET,
					new HttpEntity<>(headers),
					PositionResponse.class,
					positionId
			);
			Assertions.assertEquals(HttpStatus.NOT_FOUND, deletedPositionCheck.getStatusCode());

			// CHECK product is also gone
			ResponseEntity<ProductResponse> deletedProductCheck = restTemplate.exchange(
					"/api/v1/mode-report/products/{id}",
					HttpMethod.GET,
					new HttpEntity<>(headers),
					ProductResponse.class,
					productId
			);
			Assertions.assertEquals(HttpStatus.NOT_FOUND, deletedProductCheck.getStatusCode());
		}

		@Test
		void testBatchCreation() {
			List<PositionRequest> batch = new ArrayList<>();
			for (int i = 0; i < 5; i++) {
				PositionRequest req = new PositionRequest();
				req.setTaxpayerNumber("12345678901" + i);
				req.setSensorNumber("123_" + i);
				ZonedDateTime now = ZonedDateTime.now();
				req.setStartDate(now);
				req.setEndDate(now.plusHours(1));
				req.setVbsStart(BigDecimal.valueOf(1000 + i));
				req.setVbsEnd(BigDecimal.valueOf(1500 + i));
				req.setAStart(BigDecimal.valueOf(500 + i));
				req.setAEnd(BigDecimal.valueOf(600 + i));
				req.setPercentAlc(BigDecimal.valueOf(96.5));
				req.setBottleCountStart(BigDecimal.valueOf(1000));
				req.setBottleCountEnd(BigDecimal.valueOf(1100 + i));
				req.setTemperature(BigDecimal.valueOf(20 + i));
				req.setMode("009");
				req.setStatus("Принято в РАР");

				ProductRequest prod = new ProductRequest();
				prod.setUnitType("Фасованная");
				prod.setFullName("Product " + i);
				prod.setAlcCode("A" + i);
				prod.setProductVCode("V" + i);

				req.setProduct(prod);
				batch.add(req);
			}

			HttpEntity<List<PositionRequest>> entity = new HttpEntity<>(batch, headers);
			ResponseEntity<List<PositionResponse>> response = restTemplate.exchange(
					"/api/v1/mode-report/positions/list",
					HttpMethod.POST,
					entity,
					new ParameterizedTypeReference<>() {}
			);

			Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
			Assertions.assertEquals(5, response.getBody().size());
		}

		@Test
		void testUploadFile() throws Exception {
			MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", file.getResource());

			HttpHeaders headers = createAuthorizationHeaders();
			headers.set("Content-Type", "multipart/form-data");

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
	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class PositionProductPaginationTests {

		private HttpHeaders headers;
		private PageRequest pageRequest;
		private PositionRequest positionRequest;
		private ProductRequest productRequest;


		@BeforeEach
		void setup() {
			headers = createAuthorizationHeaders();

			pageRequest = new PageRequest();
			pageRequest.setSize(5);
			pageRequest.setNumber(0);
			pageRequest.setSortDirection("ASC");

			productRequest = new ProductRequest();
			productRequest.setUnitType(UnitType.UNPACKED.getUnitType());
			productRequest.setFullName("Водка");
			productRequest.setAlcCode("123456");
			productRequest.setProductVCode("A123");

			positionRequest = new PositionRequest();
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
			positionRequest.setMode("009");
			positionRequest.setStatus("Принято в РАР");
			positionRequest.setProduct(productRequest);
		}

		@Test
		void testCreatePositionAndProductPagination() {
			HttpEntity<PositionRequest> createEntity = new HttpEntity<>(positionRequest, headers);
			ResponseEntity<PositionResponse> createResponse = restTemplate.exchange(
					"/api/v1/mode-report/positions",
					HttpMethod.POST,
					createEntity,
					PositionResponse.class
			);
			Assertions.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
			Long positionId = createResponse.getBody().getId();
			Long productId = createResponse.getBody().getProduct().getId();

			HttpEntity<PageRequest> requestEntity = new HttpEntity<>(pageRequest, headers);
			ResponseEntity<TestPage<PositionResponse>> positionPageResponse = restTemplate.exchange(
					"/api/v1/mode-report/positions/fetch/{taxpayerNumber}",
					HttpMethod.POST,
					requestEntity,
					new ParameterizedTypeReference<TestPage<PositionResponse>>() {},
					"123456789012"
			);

			Assertions.assertEquals(HttpStatus.OK, positionPageResponse.getStatusCode());
			Assertions.assertNotNull(positionPageResponse.getBody());
			Assertions.assertFalse(positionPageResponse.getBody().getContent().isEmpty());

			HttpEntity<PageRequest> productRequestEntity = new HttpEntity<>(pageRequest, headers);
			ResponseEntity<TestPage<ProductResponse>> productPageResponse = restTemplate.exchange(
					"/api/v1/mode-report/products/fetch",
					HttpMethod.POST,
					productRequestEntity,
					new ParameterizedTypeReference<TestPage<ProductResponse>>() {}
			);

			Assertions.assertEquals(HttpStatus.OK, productPageResponse.getStatusCode());
			Assertions.assertNotNull(productPageResponse.getBody());
			Assertions.assertFalse(productPageResponse.getBody().getContent().isEmpty());
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
}

