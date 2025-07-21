package ru.centrikt.transportmonitoringservice.domain.models.navigation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import ru.centrikt.transportmonitoringservice.domain.enums.Status;
import ru.centrikt.transportmonitoringservice.domain.models.BaseEntity;
import ru.centrikt.transportmonitoringservice.domain.models.Sensor;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="navigation_reports")
public class NavigationReport extends BaseEntity {
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "level_gauge_id")
    @BatchSize(size = 50)
    private List<NavigationLevelGauge> levelGauges;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "data_id")
    private NavigationData data;
    @Column(nullable = false)
    private ZonedDateTime createdAt;
    @Column(nullable = false)
    private ZonedDateTime updatedAt;
    @Enumerated(EnumType.ORDINAL)
    private Status status;
    private String originalFilename;
}
