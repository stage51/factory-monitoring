package ru.centrikt.transportmonitoringservice.presentation.controllers.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.centrikt.transportmonitoringservice.presentation.dtos.extra.PageRequest;
import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.navigation.*;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.navigation.NavigationReportResponse;

import java.util.List;

@Tag(
        name = "Контроллер навигационных отчетов",
        description = "Позволяет работать с навигационными отчетами"
)
public interface AbstractNavigationReportController {

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр навигационных отчетов",
            description = "Выводит навигационный отчет по заданному id"
    )
    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<NavigationReportResponse> getNavigationReport(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Создание навигационных отчетов",
            description = "Создает навигационный отчет поэлементно"
    )
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<NavigationReportResponse> createNavigationReport(@RequestBody NavigationReportRequest positionRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Загрузка файла с отчетами",
            description = "Загрузка XML отчетов для хранения на FTP сервере"
    )
    @PostMapping(value = "/{id}/upload")
    ResponseEntity<?> uploadReport(@PathVariable Long id, @RequestParam("file") MultipartFile file);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Скачивание файла с отчетами",
            description = "Скачивание XML отчетов с FTP сервера"
    )
    @GetMapping(value = "/{id}/download")
    ResponseEntity<Resource> downloadReport(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Обновление навигационных отчетов",
            description = "Обновляет навигационный отчет по заданному id"
    )
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<NavigationReportResponse> updateNavigationReport(@PathVariable Long id, @RequestBody NavigationReportRequest positionRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Удаление навигационных отчетов",
            description = "Удаляет навигационный отчет по заданному id"
    )
    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> deleteNavigationReport(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр навигационных отчетов",
            description = "Выводит навигационные отчеты постранично"
    )
    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    ResponseEntity<Page<NavigationReportResponse>> getPageNavigationReports(
            @RequestBody PageRequest pageRequest
    );

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр навигационных отчетов для компании",
            description = "Выводит навигационные отчеты постранично по заданному ИНН"
    )
    @PostMapping(value = "/fetch/{taxpayerNumber}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    ResponseEntity<Page<NavigationReportResponse>> getPageNavigationReports(
            @RequestBody PageRequest pageRequest, @PathVariable String taxpayerNumber
    );

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр истории состояний транспорта",
            description = "Выводит информацию по навигации транспорта в виде списка"
    )
    @PostMapping(value = "/navigate")
    ResponseEntity<List<NavigationReportResponse>> navigate(@RequestBody NavigationAdminNavigateParamsRequest paramsRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр истории состояний транспорта для компаний",
            description = "Выводит информацию по навигации транспорта в виде списка по заданному ИНН"
    )
    @PostMapping(value = "/navigate/{taxpayerNumber}")
    ResponseEntity<List<NavigationReportResponse>> navigate(@RequestBody NavigationUserNavigateParamsRequest paramsRequest,
                                                            @PathVariable String taxpayerNumber);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Поиск состояния транспорта",
            description = "Выводит информацию по навигации транспорта"
    )
    @PostMapping(value = "/find")
    ResponseEntity<NavigationReportResponse> find(@RequestBody NavigationAdminFindParamsRequest paramsRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Поиск состояния транспорта для компаний",
            description = "Выводит информацию по навигации транспорта"
    )
    @PostMapping(value = "/find/{taxpayerNumber}")
    ResponseEntity<NavigationReportResponse> find(@RequestBody NavigationUserFindParamsRequest paramsRequest,
                                                  @PathVariable String taxpayerNumber);
}
