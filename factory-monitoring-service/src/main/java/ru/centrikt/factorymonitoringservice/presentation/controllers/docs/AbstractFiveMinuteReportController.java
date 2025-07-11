package ru.centrikt.factorymonitoringservice.presentation.controllers.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.centrikt.factorymonitoringservice.presentation.dtos.extra.PageRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.fiveminute.FiveMinuteReportRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.ReportStatusResponse;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.fiveminute.FiveMinuteReportResponse;

import java.util.List;

@Tag(
        name = "Контроллер пятиминутных отчетов",
        description = "Позволяет работать с пятиминутными отчетами"
)
public interface AbstractFiveMinuteReportController {

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр пятиминутных отчетов",
            description = "Выводит пятиминутный отчет по заданному id"
    )
    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<FiveMinuteReportResponse> getFiveMinuteReport(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Создание пятиминутных отчетов",
            description = "Создает пятиминутный отчет поэлементно"
    )
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<FiveMinuteReportResponse> createFiveMinuteReport(@RequestBody FiveMinuteReportRequest positionRequest);

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
            summary = "Обновление пятиминутных отчетов",
            description = "Обновляет пятиминутный отчет по заданному id"
    )
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<FiveMinuteReportResponse> updateFiveMinuteReport(@PathVariable Long id, @RequestBody FiveMinuteReportRequest positionRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Удаление пятиминутных отчетов",
            description = "Удаляет пятиминутный отчет по заданному id"
    )
    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> deleteFiveMinuteReport(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр пятиминутных отчетов",
            description = "Выводит пятиминутные отчеты постранично"
    )
    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    ResponseEntity<Page<FiveMinuteReportResponse>> getPageFiveMinuteReports(
            @RequestBody PageRequest pageRequest
    );

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр пятиминутных отчетов для компании",
            description = "Выводит пятиминутные отчеты постранично по заданному ИНН"
    )
    @PostMapping(value = "/fetch/{taxpayerNumber}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    ResponseEntity<Page<FiveMinuteReportResponse>> getPageFiveMinuteReports(
            @RequestBody PageRequest pageRequest, @PathVariable String taxpayerNumber
    );

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр состояний линий",
            description = "Выводит для организаций по заданному ИНН список статусов по всем комплексам и линиям в них"
    )
    @GetMapping(value = "/check")
    ResponseEntity<List<ReportStatusResponse>> checkLines(@RequestParam String taxpayerNumber);
}
