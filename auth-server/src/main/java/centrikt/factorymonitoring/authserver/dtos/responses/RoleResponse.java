package centrikt.factorymonitoring.authserver.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Schema(description = "Форма вывода информации о ролях")
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {
    private String role;
    private String description;
}
