package centrikt.factory_monitoring.daily_report.models;

import centrikt.factory_monitoring.daily_report.enums.Mode;
import centrikt.factory_monitoring.daily_report.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Table(name="positions")
public class Position extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "product_id")
    private Product product;
    @Column(nullable = false, length = 12)
    private String taxpayerNumber;
    @Column(nullable = false)
    private String controllerNumber;
    @Column(nullable = false)
    private String lineNumber;
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
    @Column(precision = 4, scale = 2)
    private BigDecimal crotonaldehyde;
    @Column(precision = 4, scale = 2)
    private BigDecimal toluene;
    @Enumerated(EnumType.ORDINAL)
    private Status status;
}
