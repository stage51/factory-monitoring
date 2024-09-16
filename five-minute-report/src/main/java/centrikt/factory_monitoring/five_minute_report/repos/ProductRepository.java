package centrikt.factory_monitoring.five_minute_report.repos;

import centrikt.factory_monitoring.five_minute_report.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
