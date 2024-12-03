package centrikt.factorymonitoring.modereport.models;

import centrikt.factorymonitoring.modereport.enums.Type;
import centrikt.factorymonitoring.modereport.enums.UnitType;
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
    private String fullName;
    @Column(nullable = false)
    private String alcCode;
    @Column(nullable = false)
    private String productVCode;
}
