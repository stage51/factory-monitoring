package centrikt.factorymonitoring.authserver.dtos.requests;

import lombok.Getter;

@Getter
public class OrganizationRequest {
    private Long userId;
    private String shortName;
    private String name;
    private String type;
    private String region;
    private String taxpayerNumber;
    private String reasonCode;
    private String address;
    private String specialEmail;
    private String specialPhone;
}
