package centrikt.factorymonitoring.authserver.models;

import centrikt.factorymonitoring.authserver.models.enums.OrganizationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "organizations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Organization extends BaseEntity {
    private String shortName;
    private String name;
    @Enumerated(EnumType.ORDINAL)
    private OrganizationType type;
    private String region;
    private String taxpayerNumber;
    private String reasonCode;
    private String address;
    private String specialEmail;
    private String specialPhone;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organization", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @BatchSize(size = 20)
    private List<Controller> controllers;
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private User user;
}