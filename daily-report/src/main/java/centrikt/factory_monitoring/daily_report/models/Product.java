package centrikt.factory_monitoring.daily_report.models;

import centrikt.factory_monitoring.daily_report.enums.Type;
import centrikt.factory_monitoring.daily_report.enums.UnitType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name="products")
public class Product extends BaseEntity {
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
