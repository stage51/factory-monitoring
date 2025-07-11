package ru.centrikt.factorymonitoringservice.domain.models.fiveminute;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.centrikt.factorymonitoringservice.domain.enums.Status;
import ru.centrikt.factorymonitoringservice.domain.models.BaseEntity;
import ru.centrikt.factorymonitoringservice.domain.models.Sensor;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="five_minute_reports")
public class FiveMinuteReport extends BaseEntity {
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "position_id")
    private FiveMinutePosition position;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;
    @Column(nullable = false)
    private ZonedDateTime createdAt;
    @Column(nullable = false)
    private ZonedDateTime updatedAt;
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Status status;
    private String originalFilename;
}
