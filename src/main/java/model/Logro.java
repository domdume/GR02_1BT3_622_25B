package model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "logro", uniqueConstraints = @UniqueConstraint(columnNames = {"miembro_id", "logro_id"}))
public class Logro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "miembro_id", nullable = false)
    private MiembroHogar miembro;

    @Column(name = "logro_id", nullable = false, length = 50)
    private String logroId;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public Logro() {}

    public Logro(MiembroHogar miembro, String logroId) {
        this.miembro = miembro;
        this.logroId = logroId;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MiembroHogar getMiembro() { return miembro; }
    public void setMiembro(MiembroHogar miembro) { this.miembro = miembro; }

    public String getLogroId() { return logroId; }
    public void setLogroId(String logroId) { this.logroId = logroId; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}

