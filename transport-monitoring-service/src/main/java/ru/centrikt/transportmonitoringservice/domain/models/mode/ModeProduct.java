package ru.centrikt.transportmonitoringservice.domain.models.mode;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.centrikt.transportmonitoringservice.domain.enums.UnitType;
import ru.centrikt.transportmonitoringservice.domain.models.BaseEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="mode_products")
public class ModeProduct extends BaseEntity {
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private UnitType unitType;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false)
    private String alcCode;
    @Column(nullable = false)
    private String productVCode;
}
