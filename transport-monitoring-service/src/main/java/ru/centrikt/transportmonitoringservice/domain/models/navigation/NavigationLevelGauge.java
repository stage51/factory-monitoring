package ru.centrikt.transportmonitoringservice.domain.models.navigation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.centrikt.transportmonitoringservice.domain.models.BaseEntity;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="navigation_level_gauges")
public class NavigationLevelGauge extends BaseEntity {
    @Column(nullable = false)
    private Long number;
    @Column(precision = 15, scale = 3)
    private BigDecimal readings;
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal temperature;
    @Column(nullable = false, precision = 3, scale = 3)
    private BigDecimal density;
}
