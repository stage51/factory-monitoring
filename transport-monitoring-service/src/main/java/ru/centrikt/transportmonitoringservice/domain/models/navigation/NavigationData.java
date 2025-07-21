package ru.centrikt.transportmonitoringservice.domain.models.navigation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.centrikt.transportmonitoringservice.domain.models.BaseEntity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="navigation_datas")
public class NavigationData extends BaseEntity {
    @Column(nullable = false)
    private ZonedDateTime navigationDate;
    @Column(nullable = false, precision = 17, scale = 15)
    private BigDecimal latitude;
    @Column(nullable = false, precision = 17, scale = 15)
    private BigDecimal longitude;
    @Column(nullable = false)
    private Long countSatellite;
    @Column(nullable = false)
    private Long accuracy;
    @Column(nullable = false)
    private Long course;
    @Column(nullable = false)
    private Long speed;
}
