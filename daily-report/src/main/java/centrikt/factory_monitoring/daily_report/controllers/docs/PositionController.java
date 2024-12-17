package centrikt.factory_monitoring.daily_report.controllers.docs;

import centrikt.factory_monitoring.daily_report.dtos.extra.PageRequestDTO;
import centrikt.factory_monitoring.daily_report.dtos.requests.PositionRequest;
import centrikt.factory_monitoring.daily_report.dtos.responses.PositionResponse;
import centrikt.factory_monitoring.daily_report.dtos.responses.ReportStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Контроллер позиций",
        description = "Позволяет работать с позициями"
)
public interface PositionController {

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр позиции",
            description = "Выводит позицию по заданному id"
    )
    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<PositionResponse> getPosition(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Создание позиции",
            description = "Создает позицию поэлементно"
    )
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<PositionResponse> createPosition(@RequestBody PositionRequest positionRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Создание нескольких позиций",
            description = "Создает позиции на основе списка из нескольких форм запросов"
    )
    @PostMapping(value = "/list", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<List<PositionResponse>> createPositions(@RequestBody List<PositionRequest> positionRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Обновление позиции",
            description = "Обновляет позицию по заданному id"
    )
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    ResponseEntity<PositionResponse> updatePosition(@PathVariable Long id, @RequestBody PositionRequest positionRequest);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Удаление позиции",
            description = "Удаляет позицию по заданному id"
    )
    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> deletePosition(@PathVariable Long id);

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр позиций",
            description = "Выводит позиции постранично"
    )
    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    ResponseEntity<Page<PositionResponse>> getPagePositions(
            @RequestBody PageRequestDTO pageRequestDTO
    );

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр позиций для компании",
            description = "Выводит позиции постранично по заданному ИНН"
    )
    @PostMapping(value = "/fetch/{taxpayerNumber}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    ResponseEntity<Page<PositionResponse>> getPagePositions(
            @RequestBody PageRequestDTO pageRequestDTO, @PathVariable String taxpayerNumber
    );

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Просмотр состояний линий",
            description = "Выводит для организвций по заданному ИНН список статусов по всем комплексам и линиям в них"
    )
    @GetMapping(value = "/check")
    ResponseEntity<List<ReportStatusResponse>> checkLines(@RequestParam String taxpayerNumber);
}
