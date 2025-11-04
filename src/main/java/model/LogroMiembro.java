package model;

import jakarta.persistence.*;

@Entity
@Table(name = "logros_miembro")
public class LogroMiembro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "miembro_id", nullable = false)
    private MiembroHogar miembro;

    @Column(name = "logro_id", nullable = false)
    private String logroId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_logro", nullable = false)
    private TipoLogro tipoLogro;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel", nullable = false)
    private TipoMedalla nivel;

    public LogroMiembro() {
    }

    public LogroMiembro(MiembroHogar miembro, String logroId, TipoLogro tipoLogro, TipoMedalla nivel) {
        this.miembro = miembro;
        this.logroId = logroId;
        this.tipoLogro = tipoLogro;
        this.nivel = nivel;
    }

    // Getters y setters
    public Long getId() {
        return id;
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

    public TipoLogro getTipoLogro() {
        return tipoLogro;
    }

    public TipoMedalla getNivel() {
        return nivel;
    }
}
