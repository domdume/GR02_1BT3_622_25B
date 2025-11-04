package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logro")
public class Logro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "miembro_id", nullable = false)
    private MiembroHogar miembro;

    @Column(name = "logro_id", nullable = false)
    private String logroId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_logro")
    private TipoLogro tipoLogro;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel")
    private TipoMedalla nivel;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    public Logro() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Logro(MiembroHogar miembro, String logroId) {
        this();
        this.miembro = miembro;
        this.logroId = logroId;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MiembroHogar getMiembro() {
        return miembro;
    }

    public void setMiembro(MiembroHogar miembro) {
        this.miembro = miembro;
    }

    public String getLogroId() {
        return logroId;
    }

    public void setLogroId(String logroId) {
        this.logroId = logroId;
    }

    public TipoLogro getTipoLogro() {
        return tipoLogro;
    }

    public void setTipoLogro(TipoLogro tipoLogro) {
        this.tipoLogro = tipoLogro;
    }

    public TipoMedalla getNivel() {
        return nivel;
    }

    public void setNivel(TipoMedalla nivel) {
        this.nivel = nivel;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
