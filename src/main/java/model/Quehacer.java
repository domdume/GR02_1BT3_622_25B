package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Quehacer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private boolean estadoCompletado;
    private LocalDateTime tiempoLimite;
    private LocalDateTime fechaFinalizacion;

    @Enumerated(EnumType.STRING)
    private Dificultad dificultad;

    @ManyToOne
    @JoinColumn(name = "miembro_hogar_id")
    private MiembroHogar miembroHogar;

    // Constructor vacío para JPA
    public Quehacer() {
    }

    public Quehacer(String nombre, Dificultad dificultad, LocalDateTime tiempoLimite) {
        this.nombre = nombre;
        this.dificultad = dificultad;
        this.tiempoLimite = tiempoLimite;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEstadoCompletado() {
        return estadoCompletado;
    }

    public void setEstadoCompletado(boolean estadoCompletado) {
        this.estadoCompletado = estadoCompletado;
    }

    public LocalDateTime getTiempoLimite() {
        return tiempoLimite;
    }

    public void setTiempoLimite(LocalDateTime tiempoLimite) {
        this.tiempoLimite = tiempoLimite;
    }

    public LocalDateTime getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(LocalDateTime fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    public void setDificultad(Dificultad dificultad) {
        this.dificultad = dificultad;
    }

    public MiembroHogar getMiembroHogar() {
        return miembroHogar;
    }

    public void setMiembroHogar(MiembroHogar miembroHogar) {
        this.miembroHogar = miembroHogar;
    }

    // Lógica de negocio
    public void marcarComoCompletado() {
        this.estadoCompletado = true;
        this.fechaFinalizacion = LocalDateTime.now();
    }

    public boolean fueCompletadoATiempo() {
        if (!estadoCompletado || fechaFinalizacion == null) {
            return false;
        }
        return fechaFinalizacion.isBefore(tiempoLimite);
    }

    @Override
    public String toString() {
        return "Quehacer{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", estadoCompletado=" + estadoCompletado +
                ", dificultad=" + dificultad +
                '}';
    }
}
