package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import service.IncentivoService;

@Entity
public class Incentivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoIncentivo tipoIncentivo;

    @ManyToOne
    @JoinColumn(name = "miembro_hogar_id")
    private MiembroHogar miembroHogar;

    @ManyToOne
    @JoinColumn(name = "quehacer_id")
    private Quehacer quehacer;

    private int puntos;
    private String descripcion;
    private LocalDateTime fechaCreacion;

    // Constructor para JPA
    public Incentivo() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Incentivo(TipoIncentivo tipo, int puntos, String descripcion, MiembroHogar miembro, Quehacer quehacer) {
        this();
        this.tipoIncentivo = tipo;
        this.puntos = puntos;
        this.descripcion = descripcion;
        this.miembroHogar = miembro;
        this.quehacer = quehacer;
    }

    public static final int PUNTOS_FACIL = 10;
    public static final int PUNTOS_MEDIO = 20;
    public static final int PUNTOS_DIFICIL = 30;
    public static final int PENALIZACION = 5;

    // La lógica de aplicar incentivos se ha movido a IncentivoService

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoIncentivo getTipoIncentivo() {
        return tipoIncentivo;
    }

    public void setTipoIncentivo(TipoIncentivo tipoIncentivo) {
        this.tipoIncentivo = tipoIncentivo;
    }

    public MiembroHogar getMiembroHogar() {
        return miembroHogar;
    }

    public void setMiembroHogar(MiembroHogar miembroHogar) {
        this.miembroHogar = miembroHogar;
    }

    public Quehacer getQuehacer() {
        return quehacer;
    }

    public void setQuehacer(Quehacer quehacer) {
        this.quehacer = quehacer;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

//    public String getDescripcion() {
//        return descripcion;
//    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }


    /**
     * Método estático de fábrica para crear y aplicar un incentivo.
     * Reemplaza los métodos anteriores y delega al IncentivoService.
     */
    public static void aplicarIncentivo(MiembroHogar miembro, Quehacer quehacer) {
        IncentivoService incentivoService = new IncentivoService();
        incentivoService.aplicarIncentivo(miembro, quehacer);
    }
}
