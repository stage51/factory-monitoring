package ru.centrikt.transportmonitoringservice.domain.models.daily;

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
@Table(name="daily_reports")
public class DailyReport extends BaseEntity {
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "position_id")
    @BatchSize(size = 50)
    private List<DailyPosition> positions;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;
    @Column(nullable = false)
    private ZonedDateTime createdAt;
    @Column(nullable = false)
    private ZonedDateTime updatedAt;
    @Enumerated(EnumType.ORDINAL)
    private Status status;
    private String originalFilename;
}
