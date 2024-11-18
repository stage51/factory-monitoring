package centrikt.factorymonitoring.authserver.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
@Getter
@Setter
public class User {
    @Id
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String timezone;
    private boolean subscribe;
    private List<String> roles; // Например, "ROLE_USER", "ROLE_ADMIN"
    private Organization organization;
}
