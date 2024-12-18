package centrikt.factorymonitoring.authserver.services;

import centrikt.factorymonitoring.authserver.dtos.messages.ReportMessage;
import centrikt.factorymonitoring.authserver.dtos.requests.OrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.OrganizationResponse;

public interface OrganizationService extends CrudService<OrganizationRequest, OrganizationResponse> {
    OrganizationResponse createOrganization(String accessToken, AuthOrganizationRequest organizationRequest);
    OrganizationResponse updateOrganization(String accessToken, AuthOrganizationRequest organizationRequest);
    void deleteOrganization(String accessToken);
    void receiveReportMessageAndSendEmail(ReportMessage reportMessage);
}
