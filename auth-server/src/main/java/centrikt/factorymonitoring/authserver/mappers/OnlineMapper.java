package centrikt.factorymonitoring.authserver.mappers;

import centrikt.factorymonitoring.authserver.dtos.responses.OnlineResponse;
import centrikt.factorymonitoring.authserver.models.Online;

public class OnlineMapper {
    public static OnlineResponse toResponse(Online online) {
        if (online == null) {
            return null;
        }
        OnlineResponse dto = OnlineResponse.builder().id(online.getId()).email(online.getEmail())
                .ipAddress(online.getIpAddress()).activeAt(online.getActiveAt())
                .build();
        return dto;
    }
}
