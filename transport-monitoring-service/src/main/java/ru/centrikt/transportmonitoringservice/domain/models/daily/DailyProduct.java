package ru.centrikt.transportmonitoringservice.domain.models.daily;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.centrikt.transportmonitoringservice.domain.enums.UnitType;
import ru.centrikt.transportmonitoringservice.domain.models.BaseEntity;

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
    private String fullName;
    @Column(nullable = false)
    private String alcCode;
    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal alcVolume;
    @Column(nullable = false)
    private String productVCode;
}
