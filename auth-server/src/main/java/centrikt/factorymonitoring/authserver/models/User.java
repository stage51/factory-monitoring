package centrikt.factorymonitoring.authserver.models;

import centrikt.factorymonitoring.authserver.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User extends BaseEntity {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String timezone;
    private boolean subscribe;
    private boolean active;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToOne
    private Organization organization;
}
