package ru.centrikt.factorymonitoringservice.domain.models.mode;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.centrikt.factorymonitoringservice.domain.enums.Mode;
import ru.centrikt.factorymonitoringservice.domain.models.BaseEntity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="mode_positions")
public class ModePosition extends BaseEntity {
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id")
    private ModeProduct product;
    @Column(nullable = false)
    private ZonedDateTime startDate;
    @Column(nullable = false)
    private ZonedDateTime endDate;
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal vbsStart;
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal vbsEnd;
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal aStart;
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal aEnd;
    @Column(nullable = false, precision = 15, scale = 1)
    private BigDecimal percentAlc;
    @Column(nullable = false, precision = 16, scale = 0)
    private BigDecimal bottleCountStart;
    @Column(nullable = false, precision = 16, scale = 0)
    private BigDecimal bottleCountEnd;
    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal temperature;
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Mode mode;
}
