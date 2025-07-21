package ru.centrikt.transportmonitoringservice.domain.models.mode;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.centrikt.transportmonitoringservice.domain.models.BaseEntity;
import ru.centrikt.transportmonitoringservice.domain.models.Sensor;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="mode_reports")
public class ModeReport extends BaseEntity {
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "position_id")
    private ModePosition position;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;
    @Column(nullable = false)
    private ZonedDateTime createdAt;
    @Column(nullable = false)
    private ZonedDateTime updatedAt;
    private String originalFilename;
}
