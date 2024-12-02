package centrikt.factorymonitoring.authserver.repos;

import centrikt.factorymonitoring.authserver.models.Online;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnlineRepository extends JpaRepository<Online, Long> {
    Page<Online> findAll(Specification<Online> specification, Pageable pageable);
}
