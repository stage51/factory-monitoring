package centrikt.factorymonitoring.authserver.controllers;

import centrikt.factorymonitoring.authserver.dtos.extra.PageRequestDTO;
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
@RequestMapping("/api/v1/auth-server/refresh_tokens")
@Slf4j
public class RefreshTokenController {

    private RefreshTokenService<RefreshTokenResponse> refreshTokenService;

    public RefreshTokenController(RefreshTokenService<RefreshTokenResponse> refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }
    @Autowired
    public void setRefreshTokenService(RefreshTokenService<RefreshTokenResponse> refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<Page<RefreshTokenResponse>> getPage(
            @RequestBody PageRequestDTO pageRequestDTO
    ) {
        log.info("Fetching page positions with filters: {}, dateRanges: {}", pageRequestDTO.getFilters(), pageRequestDTO.getDateRanges());
        Page<RefreshTokenResponse> users = refreshTokenService.getPage(
                pageRequestDTO.getSize(),
                pageRequestDTO.getNumber(),
                pageRequestDTO.getSortBy(),
                pageRequestDTO.getSortDirection(),
                pageRequestDTO.getFilters(),
                pageRequestDTO.getDateRanges()
        );
        return ResponseEntity.ok(users);
    }
}
