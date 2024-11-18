package centrikt.factorymonitoring.authserver.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "organizations")
@Getter
@Setter
public class Organization {
    @Id
    private String id;
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