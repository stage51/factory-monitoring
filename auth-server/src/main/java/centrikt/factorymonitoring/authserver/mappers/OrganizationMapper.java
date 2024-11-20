package centrikt.factorymonitoring.authserver.mappers;


import centrikt.factorymonitoring.authserver.dtos.requests.OrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.OrganizationResponse;
import centrikt.factorymonitoring.authserver.models.Organization;
import centrikt.factorymonitoring.authserver.models.User;

public class OrganizationMapper {

    public static OrganizationResponse toResponse(Organization organization) {
        if (organization == null) {
            return null;
        }
        OrganizationResponse dto = OrganizationResponse.builder().id(organization.getId()).createdAt(organization.getCreatedAt()).updatedAt(organization.getUpdatedAt())
                .shortName(organization.getShortName()).name(organization.getName()).type(organization.getType()).region(organization.getRegion())
                .taxpayerNumber(organization.getTaxpayerNumber()).reasonCode(organization.getReasonCode()).address(organization.getAddress())
                .specialEmail(organization.getSpecialEmail()).specialPhone(organization.getSpecialPhone())
                .build();
        return dto;
    }

    public static Organization toEntity(OrganizationRequest organizationRequest, User user) {
        if (organizationRequest == null) {
            return null;
        }
        Organization organization = new Organization();
        organization.setUser(user);
        organization.setShortName(organizationRequest.getShortName());
        organization.setName(organizationRequest.getName());
        organization.setType(organizationRequest.getType());
        organization.setRegion(organizationRequest.getRegion());
        organization.setTaxpayerNumber(organizationRequest.getTaxpayerNumber());
        organization.setReasonCode(organizationRequest.getReasonCode());
        organization.setAddress(organizationRequest.getAddress());
        organization.setSpecialEmail(organizationRequest.getSpecialEmail());
        organization.setSpecialPhone(organizationRequest.getSpecialPhone());
        return organization;
    }
}

