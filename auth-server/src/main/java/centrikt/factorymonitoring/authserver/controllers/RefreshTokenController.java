package centrikt.factorymonitoring.authserver.controllers;

import centrikt.factorymonitoring.authserver.dtos.extra.PageRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.RefreshTokenResponse;
import centrikt.factorymonitoring.authserver.services.RefreshTokenService;
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
@RequestMapping("/api/v1/auth-server/refresh-tokens")
@Slf4j
public class RefreshTokenController implements centrikt.factorymonitoring.authserver.controllers.docs.RefreshTokenController {

    private RefreshTokenService<RefreshTokenResponse> refreshTokenService;

    public RefreshTokenController(RefreshTokenService<RefreshTokenResponse> refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
        log.info("RefreshTokenController initialized");
    }

    @Autowired
    public void setRefreshTokenService(RefreshTokenService<RefreshTokenResponse> refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
        log.debug("RefreshTokenService set");
    }

    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<Page<RefreshTokenResponse>> getPage(@RequestBody PageRequest pageRequest) {
        log.info("Fetching refresh token page with filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());
        Page<RefreshTokenResponse> tokens = refreshTokenService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );
        log.debug("Fetched {} refresh token records", tokens.getNumberOfElements());
        return ResponseEntity.ok(tokens);
    }
}
