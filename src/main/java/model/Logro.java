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
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_logro")
    private TipoLogro tipoLogro;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_medalla")
    private TipoMedalla nivel;

    @Column(name = "tareas_requeridas")
    private int tareasRequeridas;

    // Constructor por defecto requerido por JPA
    public Logro() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor para crear logros con información básica
    public Logro(MiembroHogar miembro, String logroId) {
        this.miembro = miembro;
        this.logroId = logroId;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor completo para crear logros
    public Logro(String logroId, TipoLogro tipoLogro, TipoMedalla nivel, int tareasRequeridas) {
        this.logroId = logroId;
        this.tipoLogro = tipoLogro;
        this.nivel = nivel;
        this.tareasRequeridas = tareasRequeridas;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Métodos getter y setter
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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

    public int getTareasRequeridas() {
        return tareasRequeridas;
    }

    public void setTareasRequeridas(int tareasRequeridas) {
        this.tareasRequeridas = tareasRequeridas;
    }

    // Método para verificar si se cumple el logro
    public boolean seCumpleConTareas(int tareasCompletadas) {
        return tareasCompletadas >= tareasRequeridas;
    }
}

