package centrikt.factorymonitoring.mailservice.dtos.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage{
    private String[] toAddresses;
    private String subject;
    private String message;
}