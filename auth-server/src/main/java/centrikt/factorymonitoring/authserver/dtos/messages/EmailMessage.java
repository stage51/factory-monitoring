package centrikt.factorymonitoring.authserver.dtos.messages;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {
    private String toAddress;
    private String subject;
    private String message;
}