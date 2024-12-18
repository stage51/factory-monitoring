package centrikt.factorymonitoring.authserver.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "organizations")
@Data
public class Organization extends BaseEntity {
    private String shortName;
    private String name;
    private String type;
    private String region;
    private String taxpayerNumber;
    private String reasonCode;
    private String address;
    private String specialEmail;
    private String specialPhone;
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private User user;
}