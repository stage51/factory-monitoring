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
import ru.centrikt.factorymonitoringservice.presentation.dtos.requests.mode.ModeReportRequest;
import ru.centrikt.factorymonitoringservice.presentation.dtos.responses.mode.ModeReportResponse;

import java.util.List;

@Tag(
        name = "Контроллер сессий",
        description = "Позволяет работать с сессиями"
)
public interface AbstractModeReportController {

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр сессий",
            description = "Выводит сессию по заданному id"
    )
    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<ModeReportResponse> getModeReport(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Создание сессии",
            description = "Создает сессию поэлементно"
    )
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<ModeReportResponse> createModeReport(@RequestBody ModeReportRequest modeReportRequest);

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
            summary = "Обновление сессии",
            description = "Обновляет сессию по заданному id"
    )
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<ModeReportResponse> updateModeReport(@PathVariable Long id, @RequestBody ModeReportRequest modeReportRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Удаление сессии",
            description = "Удаляет сессию по заданному id"
    )
    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> deleteModeReport(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр сессий",
            description = "Выводит сессии постранично"
    )
    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    ResponseEntity<Page<ModeReportResponse>> getPageModeReports(
            @RequestBody PageRequest pageRequest
    );

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр сессий для компании",
            description = "Выводит сессии постранично по заданному ИНН"
    )
    @PostMapping(value = "/fetch/{taxpayerNumber}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    ResponseEntity<Page<ModeReportResponse>> getPageModeReports(
            @RequestBody PageRequest pageRequest, @PathVariable String taxpayerNumber
    );
}
