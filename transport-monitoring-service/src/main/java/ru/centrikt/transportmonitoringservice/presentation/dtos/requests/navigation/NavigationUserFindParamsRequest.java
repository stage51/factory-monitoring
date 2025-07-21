package ru.centrikt.transportmonitoringservice.presentation.dtos.requests.navigation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Форма на запрос поиска навигации для пользователя")
public class NavigationUserFindParamsRequest {
    @Schema(description = "Фильтр по гос номеру")
    private String govNumber;
}
