package centrikt.factorymonitoring.authserver.dtos.requests.admin;

import lombok.Getter;

@Getter
public class AdminUserRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String timezone;
    private boolean active;
    private boolean subscribe;
    private String role;
}
