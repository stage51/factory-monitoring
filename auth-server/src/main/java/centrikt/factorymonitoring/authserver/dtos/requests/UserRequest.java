package centrikt.factorymonitoring.authserver.dtos.requests;

import lombok.Getter;

@Getter
public class UserRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String timezone;
    private boolean subscribe;
}
