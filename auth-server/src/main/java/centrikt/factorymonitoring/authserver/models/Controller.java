package centrikt.factorymonitoring.authserver.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "controllers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Controller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String serialNumber;
    @Column(nullable = false)
    private String govNumber;
    @Column(nullable = false)
    private String guid;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
}
