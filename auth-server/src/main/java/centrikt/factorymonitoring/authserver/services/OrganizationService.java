package centrikt.factorymonitoring.authserver.services;

import centrikt.factorymonitoring.authserver.dtos.messages.ReportMessage;
import centrikt.factorymonitoring.authserver.dtos.requests.ControllerRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.OrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.ControllerResponse;
import centrikt.factorymonitoring.authserver.dtos.responses.OrganizationResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface OrganizationService extends CrudService<OrganizationRequest, OrganizationResponse> {
    OrganizationResponse createOrganization(String accessToken, AuthOrganizationRequest organizationRequest);
    OrganizationResponse updateOrganization(String accessToken, AuthOrganizationRequest organizationRequest);
    void deleteOrganization(String accessToken);
    void receiveReportMessageAndSendEmail(ReportMessage reportMessage);
    Page<ControllerResponse> getControllers(String accessToken, int size, int number, String sortBy, String sortDirection, Map<String, String> filters, Map<String, String> dateRanges);
    ControllerResponse createController(String accessToken, ControllerRequest controllerRequest);
    void deleteController(String accessToken, Long id);
}
