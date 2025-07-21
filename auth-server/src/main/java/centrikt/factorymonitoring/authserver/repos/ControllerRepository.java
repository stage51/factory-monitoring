package centrikt.factorymonitoring.authserver.repos;

import centrikt.factorymonitoring.authserver.models.Controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ControllerRepository extends JpaRepository<Controller, Long> {
    Page<Controller> findAll(Specification<Controller> specification, Pageable pageable);
}
