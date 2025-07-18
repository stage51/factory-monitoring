package centrikt.factory_monitoring.five_minute_report.models;

import centrikt.factory_monitoring.five_minute_report.enums.Mode;
import centrikt.factory_monitoring.five_minute_report.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "positions")
public class Position extends BaseEntity{
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id")
    private Product product;
    @Column(nullable = false)
    private String controllerNumber;
    @Column(nullable = false)
    private String lineNumber;
    @Column(nullable = false, length = 12)
    private String taxpayerNumber;
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
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Status status;
}
