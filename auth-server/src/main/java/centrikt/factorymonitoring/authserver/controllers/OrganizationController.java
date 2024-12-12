package centrikt.factorymonitoring.authserver.controllers;

import centrikt.factorymonitoring.authserver.dtos.extra.PageRequestDTO;
import centrikt.factorymonitoring.authserver.dtos.requests.OrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.OrganizationResponse;
import centrikt.factorymonitoring.authserver.services.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth-server/organizations")
@Slf4j
public class  OrganizationController {
    private OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }
    @Autowired
    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<OrganizationResponse> getOrganization(@PathVariable Long id) {
        log.info("Fetching organization with id: {}", id);
        OrganizationResponse organization = organizationService.get(id);
        return ResponseEntity.ok(organization);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<OrganizationResponse> createOrganization(@RequestBody OrganizationRequest organizationRequest) {
        log.info("Creating new organization: {}", organizationRequest);
        OrganizationResponse createdOrganization = organizationService.create(organizationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrganization);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<OrganizationResponse> updateOrganization(@PathVariable Long id, @RequestBody OrganizationRequest organizationRequest) {
        log.info("Updating organization with id: {}", id);
        OrganizationResponse updatedOrganization = organizationService.update(id, organizationRequest);
        return ResponseEntity.ok(updatedOrganization);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        log.info("Deleting organization with id: {}", id);
        organizationService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<Page<OrganizationResponse>> getPage(
            @RequestBody PageRequestDTO pageRequestDTO
    ) {
        log.info("Fetching page positions with filters: {}, dateRanges: {}", pageRequestDTO.getFilters(), pageRequestDTO.getDateRanges());
        Page<OrganizationResponse> organizations = organizationService.getPage(
                pageRequestDTO.getSize(),
                pageRequestDTO.getNumber(),
                pageRequestDTO.getSortBy(),
                pageRequestDTO.getSortDirection(),
                pageRequestDTO.getFilters(),
                pageRequestDTO.getDateRanges()
        );
        return ResponseEntity.ok(organizations);
    }

    @PostMapping("/profile")
    public ResponseEntity<OrganizationResponse> createOrganization(@RequestHeader("Authorization") String authorizationHeader, @RequestBody AuthOrganizationRequest request) {
        String accessToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        return ResponseEntity.ok(organizationService.createOrganization(accessToken, request));
    }
    @PutMapping("/profile")
    public ResponseEntity<OrganizationResponse> updateOrganization(@RequestHeader("Authorization") String authorizationHeader, @RequestBody AuthOrganizationRequest request) {
        String accessToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        return ResponseEntity.ok(organizationService.updateOrganization(accessToken, request));
    }
    @DeleteMapping("/profile")
    public ResponseEntity<OrganizationResponse> deleteOrganization(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        organizationService.deleteOrganization(accessToken);
        return ResponseEntity.noContent().build();
    }
}
