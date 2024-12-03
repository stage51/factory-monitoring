package centrikt.factorymonitoring.authserver.dtos.responses;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleResponse {
    private String role;
    private String description;
}
