package centrikt.factorymonitoring.authserver.dtos.requests;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelpRequest {
    private String email;
    private String message;
}
