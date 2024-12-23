package centrikt.factorymonitoring.authserver.models;

import centrikt.factorymonitoring.authserver.models.enums.ReportNotification;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Table(name = "settings")
@Data
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    private User user;
    @Column(nullable = false)
    private String timezone;
    @Column(nullable = false)
    private boolean subscribe;
    @Enumerated(EnumType.STRING)
    private List<ReportNotification> reportNotifications;
    private String avatarUrl;
}
