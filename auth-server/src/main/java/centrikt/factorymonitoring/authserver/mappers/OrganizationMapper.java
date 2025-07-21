package centrikt.factorymonitoring.authserver.mappers;


import centrikt.factorymonitoring.authserver.configs.DateTimeConfig;
import centrikt.factorymonitoring.authserver.dtos.requests.OrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.requests.users.AuthOrganizationRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.OrganizationResponse;
import centrikt.factorymonitoring.authserver.models.Organization;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.models.enums.OrganizationType;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class OrganizationMapper {

    public static OrganizationResponse toResponse(Organization organization) {
        if (organization == null) {
            return null;
        }
        OrganizationResponse dto = OrganizationResponse.builder().id(organization.getId()).userId(organization.getUser().getId()).createdAt(organization.getCreatedAt()).updatedAt(organization.getUpdatedAt())
                .shortName(organization.getShortName()).name(organization.getName()).type(organization.getType().getDescription()).region(organization.getRegion())
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
        organization.setCreatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        organization.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        return getOrganization(user, organization, organizationRequest.getShortName(), organizationRequest.getName(), organizationRequest.getType(), organizationRequest.getRegion(), organizationRequest.getTaxpayerNumber(), organizationRequest.getReasonCode(), organizationRequest.getAddress(), organizationRequest.getSpecialEmail(), organizationRequest.getSpecialPhone());
    }

    public static Organization toEntityUpdate(OrganizationRequest organizationRequest, User user, Organization organization) {
        if (organizationRequest == null) {
            return null;
        }
        organization.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        return getOrganization(user, organization, organizationRequest.getShortName(), organizationRequest.getName(), organizationRequest.getType(), organizationRequest.getRegion(), organizationRequest.getTaxpayerNumber(), organizationRequest.getReasonCode(), organizationRequest.getAddress(), organizationRequest.getSpecialEmail(), organizationRequest.getSpecialPhone());
    }

    public static Organization toEntityFromCreateRequest(AuthOrganizationRequest organizationRequest, User user) {
        if (organizationRequest == null) {
            return null;
        }
        Organization organization = new Organization();
        organization.setCreatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        organization.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        return getOrganization(user, organization, organizationRequest.getShortName(), organizationRequest.getName(), organizationRequest.getType(), organizationRequest.getRegion(), organizationRequest.getTaxpayerNumber(), organizationRequest.getReasonCode(), organizationRequest.getAddress(), organizationRequest.getSpecialEmail(), organizationRequest.getSpecialPhone());
    }

    public static Organization toEntityFromUpdateRequest(Organization existingOrganization, AuthOrganizationRequest organizationRequest) {
        if (organizationRequest == null) {
            return null;
        }
        existingOrganization.setUpdatedAt(ZonedDateTime.now(ZoneId.of(DateTimeConfig.getDefaultValue())));
        existingOrganization.setShortName(organizationRequest.getShortName());
        existingOrganization.setName(organizationRequest.getName());
        existingOrganization.setType(OrganizationType.fromDescription(organizationRequest.getType()));
        existingOrganization.setRegion(organizationRequest.getRegion());
        existingOrganization.setTaxpayerNumber(organizationRequest.getTaxpayerNumber());
        existingOrganization.setReasonCode(organizationRequest.getReasonCode());
        existingOrganization.setAddress(organizationRequest.getAddress());
        existingOrganization.setSpecialEmail(organizationRequest.getSpecialEmail());
        existingOrganization.setSpecialPhone(organizationRequest.getSpecialPhone());
        return existingOrganization;
    }

    private static Organization getOrganization(User user, Organization organization, String shortName, String name, String type, String region, String taxpayerNumber, String reasonCode, String address, String specialEmail, String specialPhone) {
        organization.setUser(user);
        organization.setShortName(shortName);
        organization.setName(name);
        organization.setType(OrganizationType.fromDescription(type));
        organization.setRegion(region);
        organization.setTaxpayerNumber(taxpayerNumber);
        organization.setReasonCode(reasonCode);
        organization.setAddress(address);
        organization.setSpecialEmail(specialEmail);
        organization.setSpecialPhone(specialPhone);
        return organization;
    }
}

