package ru.centrikt.transportmonitoringservice.presentation.dtos.requests.navigation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Форма на запрос истории навигации для пользователя")
public class NavigationUserNavigateParamsRequest {
    @Schema(description = "Фильтр по гос номеру")
    private String govNumber;
    @Schema(description = "Фильтр по дате, который выводит значения между двумя заданным датам в формате ISO. Если нет первой или второй даты и их значение null, фильтрует по наименьшей дате в системе или наибольшей соответственно")
    private String navigationDate;
}
