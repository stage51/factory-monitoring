package centrikt.factorymonitoring.authserver.repos;

import centrikt.factorymonitoring.authserver.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}