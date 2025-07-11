package ru.centrikt.factorymonitoringservice.domain.models.fiveminute;

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
@Table(name = "five_minute_positions")
public class FiveMinutePosition extends BaseEntity {
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id")
    private FiveMinuteProduct product;
    @Column(nullable = false)
    private ZonedDateTime controlDate;
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal vbsControl;
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal aControl;
    @Column(nullable = false, precision = 15, scale = 1)
    private BigDecimal percentAlc;
    @Column(nullable = false, precision = 16, scale = 0)
    private BigDecimal bottleCountControl;
    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal temperature;
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Mode mode;
}
