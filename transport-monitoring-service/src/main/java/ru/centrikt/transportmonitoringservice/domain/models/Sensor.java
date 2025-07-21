package ru.centrikt.transportmonitoringservice.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="sensors")
public class Sensor extends BaseEntity {
    @Column(nullable = false)
    private String organizationName;
    @Column(nullable = false)
    private String govNumber;
    @Column(nullable = false, length = 12)
    private String taxpayerNumber;
    @Column(nullable = false)
    private String guid;
}
