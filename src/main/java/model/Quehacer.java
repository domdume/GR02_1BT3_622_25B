package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Entity
public class Quehacer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private boolean estadoCompletado;
    private LocalDateTime tiempoLimite;
    private LocalDateTime fechaFinalizacion;
    private Dificultad dificultad; // Campo agregado según diagrama UML

    @ManyToOne
    @JoinColumn(name = "miembro_hogar_id")
    private MiembroHogar miembroHogar;

    private String recompensa; // Ejemplo: "5 puntos"
    private String penalizacion; // Ejemplo: "No completado a tiempo"
    private boolean estadoFinalizado; // Indica si el quehacer está finalizado
    
    @Transient
    private int puntosEnEseMomento; // Campo calculado para mostrar puntos progresivos

    // Constructor vacío para JPA
    public Quehacer() {
    }

    public Quehacer(String nombre, LocalDateTime tiempoLimite, Dificultad dificultad) {
        this.nombre = nombre;
        this.tiempoLimite = tiempoLimite;
        this.dificultad = dificultad;
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

    public Dificultad getDificultad() {
        return dificultad;
    }

    public void setDificultad(Dificultad dificultad) {
        this.dificultad = dificultad;
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

    public MiembroHogar getMiembroHogar() {
        return miembroHogar;
    }

    public void setMiembroHogar(MiembroHogar miembroHogar) {
        this.miembroHogar = miembroHogar;
    }

    public String getRecompensa() {
        return recompensa;
    }

    public void setRecompensa(String recompensa) {
        this.recompensa = recompensa;
    }

//    public String getPenalizacion() {
//        return penalizacion;
//    }

    public void setPenalizacion(String penalizacion) {
        this.penalizacion = penalizacion;
    }

    public boolean isEstadoFinalizado() {
        return estadoFinalizado;
    }

    public void setEstadoFinalizado(boolean estadoFinalizado) {
        this.estadoFinalizado = estadoFinalizado;
    }

    // Lógica de negocio
    
    // Métodos según diagrama UML
    public boolean estaCompletado() {
        return this.estadoCompletado;
    }

    public void marcarCompletado() {
        this.estadoCompletado = true;
        this.estadoFinalizado = true;
        this.fechaFinalizacion = LocalDateTime.now();
    }

    // Método para marcado completo manual (usado por el sistema web)
    public void marcarComoCompletado() {
        this.estadoCompletado = true;
        this.fechaFinalizacion = LocalDateTime.now();
    }

    public boolean fueCompletadoATiempo() {
        if (!estadoCompletado || fechaFinalizacion == null) {
            return false;
        }
        return fechaFinalizacion.isBefore(tiempoLimite) || fechaFinalizacion.isEqual(tiempoLimite);
    }

//    @Transient
//    public int getRewardPoints() {
//        if (!estadoCompletado || !fueCompletadoATiempo()) {
//            return 0;
//        }
//        return 20; // Puntos fijos para todos los quehaceres
//
//    }

    public boolean estaVencido() {
        return !estadoCompletado && LocalDateTime.now().isAfter(tiempoLimite);
    }


    public int getPuntosEnEseMomento() {
        return puntosEnEseMomento;
    }

    public void setPuntosEnEseMomento(int puntosEnEseMomento) {
        this.puntosEnEseMomento = puntosEnEseMomento;
    }

    @Override
    public String toString() {
        return "Quehacer{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", estadoCompletado=" + estadoCompletado +
                '}';
    }
}
