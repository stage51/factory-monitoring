package centrikt.factory_monitoring.five_minute_report.services;


import centrikt.factory_monitoring.five_minute_report.dtos.requests.ProductRequest;
import centrikt.factory_monitoring.five_minute_report.dtos.responses.ProductResponse;

public interface ProductService extends ReadService<ProductRequest, ProductResponse> {
}
