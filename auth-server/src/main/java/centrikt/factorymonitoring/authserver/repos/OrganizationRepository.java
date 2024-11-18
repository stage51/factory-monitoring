package centrikt.factorymonitoring.authserver.repos;

import centrikt.factorymonitoring.authserver.models.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrganizationRepository extends MongoRepository<Organization, String> {
}