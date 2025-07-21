package centrikt.factorymonitoring.authserver.mappers;

import centrikt.factorymonitoring.authserver.dtos.requests.ControllerRequest;
import centrikt.factorymonitoring.authserver.dtos.responses.ControllerResponse;
import centrikt.factorymonitoring.authserver.models.Controller;
import centrikt.factorymonitoring.authserver.models.Organization;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ControllerMapper {
    public static ControllerResponse toResponse(Controller controller) {
        if (controller == null) {
            return null;
        }
        ControllerResponse dto = ControllerResponse.builder()
                .id(controller.getId())
                .serialNumber(controller.getSerialNumber())
                .govNumber(controller.getGovNumber())
                .guid(controller.getGuid())
                .build();
        return dto;
    }

    public static Controller toEntity(ControllerRequest controllerRequest, Organization organization) {
        if (controllerRequest == null) {
            return null;
        }
        Controller controller = new Controller();
        controller.setGovNumber(controllerRequest.getGovNumber());
        controller.setSerialNumber(controllerRequest.getSerialNumber());
        controller.setGuid(controllerRequest.getGuid());
        controller.setOrganization(organization);
        return controller;
    }
}
