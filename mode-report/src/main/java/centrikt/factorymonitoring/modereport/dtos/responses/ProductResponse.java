package centrikt.factorymonitoring.modereport.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String unitType;
    private String fullName;
    private String alcCode;
    private String productVCode;
}
