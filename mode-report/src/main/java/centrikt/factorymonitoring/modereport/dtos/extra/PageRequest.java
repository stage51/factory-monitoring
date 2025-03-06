package centrikt.factorymonitoring.modereport.dtos.extra;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Запрос на оправку страницы")
public class PageRequest {
    @Schema(description = "Размер страницы")
    private int size;
    @Schema(description = "Номер страницы с 0")
    private int number;
    @Schema(description = "Поле сортировки")
    private String sortBy;
    @Schema(description = "Направление сортировки: ASC, DESC")
    private String sortDirection;
    @Schema(description = "Фильтр по полю и значению")
    private Map<String, String> filters = new HashMap<>();
    @Schema(description = "Фильтр по датам, который выводит значения между двумя заданным датам в формате ISO. Если нет первой или второй даты и их значение null, фильтрует по наименьшей дате в системе или наибольшей соответсвенно")
    private Map<String, String> dateRanges = new HashMap<>();
}


