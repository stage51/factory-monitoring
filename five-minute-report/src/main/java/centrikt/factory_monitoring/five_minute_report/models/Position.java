package centrikt.factory_monitoring.five_minute_report.models;

import centrikt.factory_monitoring.five_minute_report.enums.Mode;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "positions")
public class Position extends BaseEntity{
    @ManyToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "product_id")
    private Product product;
    @Column(nullable = false)
    private LocalDateTime controlDate;
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
