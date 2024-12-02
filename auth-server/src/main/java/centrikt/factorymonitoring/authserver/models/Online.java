package centrikt.factorymonitoring.authserver.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Entity
@Data
@Table(name = "onlines")
public class Online {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String ipAddress;
    private ZonedDateTime activeAt;
}
