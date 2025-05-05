package centrikt.factorymonitoring.authserver.controllers;

import centrikt.factorymonitoring.authserver.dtos.extra.PageRequest;
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
public class OrganizationController implements centrikt.factorymonitoring.authserver.controllers.docs.OrganizationController {
    private OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
        log.info("OrganizationController initialized");
    }

    @Autowired
    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
        log.debug("OrganizationService set");
    }

    @GetMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<OrganizationResponse> getOrganization(@PathVariable Long id) {
        log.info("Fetching organization with id: {}", id);
        OrganizationResponse organization = organizationService.get(id);
        log.debug("Fetched organization: {}", organization);
        return ResponseEntity.ok(organization);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<OrganizationResponse> createOrganization(@RequestBody OrganizationRequest organizationRequest) {
        log.info("Creating new organization: {}", organizationRequest);
        OrganizationResponse createdOrganization = organizationService.create(organizationRequest);
        log.debug("Created organization: {}", createdOrganization);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrganization);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<OrganizationResponse> updateOrganization(@PathVariable Long id, @RequestBody OrganizationRequest organizationRequest) {
        log.info("Updating organization with id: {}", id);
        OrganizationResponse updatedOrganization = organizationService.update(id, organizationRequest);
        log.debug("Updated organization: {}", updatedOrganization);
        return ResponseEntity.ok(updatedOrganization);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        log.info("Deleting organization with id: {}", id);
        organizationService.delete(id);
        log.debug("Deleted organization with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/fetch",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<Page<OrganizationResponse>> getPage(@RequestBody PageRequest pageRequest) {
        log.info("Fetching page with filters: {}, dateRanges: {}", pageRequest.getFilters(), pageRequest.getDateRanges());
        Page<OrganizationResponse> organizations = organizationService.getPage(
                pageRequest.getSize(),
                pageRequest.getNumber(),
                pageRequest.getSortBy(),
                pageRequest.getSortDirection(),
                pageRequest.getFilters(),
                pageRequest.getDateRanges()
        );
        log.debug("Fetched page with {} organizations", organizations.getContent().size());
        return ResponseEntity.ok(organizations);
    }

    @PostMapping("/profile")
    public ResponseEntity<OrganizationResponse> createOrganization(@RequestHeader("Authorization") String authorizationHeader, @RequestBody AuthOrganizationRequest request) {
        String accessToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        log.info("Creating organization for profile with accessToken: {}", accessToken);
        OrganizationResponse response = organizationService.createOrganization(accessToken, request);
        log.debug("Created organization profile: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<OrganizationResponse> updateOrganization(@RequestHeader("Authorization") String authorizationHeader, @RequestBody AuthOrganizationRequest request) {
        String accessToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        log.info("Updating organization for profile with accessToken: {}", accessToken);
        OrganizationResponse response = organizationService.updateOrganization(accessToken, request);
        log.debug("Updated organization profile: {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<OrganizationResponse> deleteOrganization(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
        log.info("Deleting organization profile with accessToken: {}", accessToken);
        organizationService.deleteOrganization(accessToken);
        log.debug("Deleted organization profile");
        return ResponseEntity.noContent().build();
    }
}
