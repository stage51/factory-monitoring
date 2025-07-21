package centrikt.factorymonitoring.authserver.controllers;

import centrikt.factorymonitoring.authserver.dtos.extra.PageRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.OnlineResponse;
import centrikt.factorymonitoring.authserver.services.OnlineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth-server/onlines")
@Slf4j
public class OnlineController implements centrikt.factorymonitoring.authserver.controllers.docs.OnlineController {

    private OnlineService<OnlineResponse> onlineService;

    public OnlineController(OnlineService<OnlineResponse> onlineService) {
        this.onlineService = onlineService;
        log.info("OnlineController initialized");
    }

    @Autowired
    public void setOnlineService(OnlineService<OnlineResponse> onlineService) {
        this.onlineService = onlineService;
        log.debug("OnlineService set");
    }

    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<Page<OnlineResponse>> getPage(@RequestBody PageRequest pageRequest) {
        log.debug("Fetching page with size: {}, number: {}, sortBy: {}, sortDirection: {}",
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection());
        log.info("Applying filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());

        Page<OnlineResponse> users = onlineService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );

        log.debug("Fetched {} elements", users.getNumberOfElements());
        return ResponseEntity.ok(users);
    }
}

