package ru.centrikt.factorymonitoringservice.domain.models.daily;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.centrikt.factorymonitoringservice.domain.enums.Type;
import ru.centrikt.factorymonitoringservice.domain.enums.UnitType;
import ru.centrikt.factorymonitoringservice.domain.models.BaseEntity;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="daily_products")
public class DailyProduct extends BaseEntity {
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private UnitType unitType;
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Type type;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false)
    private String shortName;
    @Column(nullable = false)
    private String alcCode;
    @Column(precision = 15, scale = 3)
    private BigDecimal capacity;
    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal alcVolume;
    @Column(nullable = false)
    private String productVCode;
}
