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


    @Column(name = "tareas_requeridas")
    private int tareasRequeridas;

    // Constructor por defecto requerido por JPA
    public Logro() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Identificadores de emblemas/achievements usados por la lógica de negocio
    public static final String EMBLEMA_APRENDIZ_CONSTANTE = "EMBLEMA_APRENDIZ_CONSTANTE";
    public static final String EMBLEMA_EXPLORADOR_PERSISTENTE = "EMBLEMA_EXPLORADOR_PERSISTENTE";
    public static final String EMBLEMA_MAESTRO_QUEHACERES = "EMBLEMA_MAESTRO_QUEHACERES";

    // Constructor para crear logros con información básica
    public Logro(MiembroHogar miembro, String logroId) {
        this.miembro = miembro;
        this.logroId = logroId;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor completo para crear logros
    public Logro(String logroId, TipoLogro tipoLogro, int tareasRequeridas) {
        this.logroId = logroId;
        this.tipoLogro = tipoLogro;
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

    /**
     * Nombre legible para mostrar en vistas. Si logroId corresponde a un código
     * conocido, se devuelve el nombre humano; en caso contrario se devuelve el
     * propio logroId.
     */
    public String getNombre() {
        if (this.logroId == null) return "";
        switch (this.logroId) {
            case EMBLEMA_APRENDIZ_CONSTANTE:
                return "Aprendiz Constante";
            case EMBLEMA_EXPLORADOR_PERSISTENTE:
                return "Explorador Persistente";
            case EMBLEMA_MAESTRO_QUEHACERES:
                return "Maestro de los Quehaceres";
            // Logros de racha
            case "LOGRO_3":
                return "Chispazo";
            case "LOGRO_7":
                return "Semana Perfecta";
            default:
                // Si el logroId ya es un texto legible, devolverlo tal cual
                return this.logroId;
        }
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

    public TipoLogro getTipo() {
        return tipoLogro;
    }
}
