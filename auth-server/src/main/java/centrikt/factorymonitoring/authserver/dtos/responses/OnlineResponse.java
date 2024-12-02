package centrikt.factorymonitoring.authserver.dtos.responses;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class OnlineResponse {
    private Long id;
    private String email;
    private String ipAddress;
    private ZonedDateTime activeAt;
}
