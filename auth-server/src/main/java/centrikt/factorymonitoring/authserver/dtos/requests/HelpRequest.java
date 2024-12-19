package centrikt.factorymonitoring.authserver.dtos.requests;


import lombok.Data;

@Data
public class HelpRequest {
    private String email;
    private String message;
}
