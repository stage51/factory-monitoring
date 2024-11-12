package centrikt.factory_monitoring.daily_report.services;

import centrikt.factory_monitoring.daily_report.dtos.requests.ProductRequest;
import centrikt.factory_monitoring.daily_report.dtos.responses.ProductResponse;
import centrikt.factory_monitoring.daily_report.models.Product;

public interface ProductService extends CrudService<ProductRequest, ProductResponse> {
}
