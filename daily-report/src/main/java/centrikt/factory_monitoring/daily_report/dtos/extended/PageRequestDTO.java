package centrikt.factory_monitoring.daily_report.dtos.extended;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {
    private int size;
    private int number;
    private String sortBy;
    private String sortDirection;
    private Map<String, String> filters = new HashMap<>();
    private Map<String, String> dateRanges = new HashMap<>();
}


