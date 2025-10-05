package model;

import java.time.LocalDateTime;

public class Quehacer{
    private String nombre;
    private boolean estadoCompletado;
    private LocalDateTime tiempoLimite;
    private LocalDateTime fechaFinalizacion;
    private Dificultad dificultad;

    public Quehacer(String nombre, Dificultad dificultad, LocalDateTime tiempoLimite) {
        this.nombre = nombre;
        this.tiempoLimite = tiempoLimite;
        this.dificultad = dificultad;
    }
    public void marcarComoCompletado(){
        this.estadoCompletado = true;
        this.fechaFinalizacion = LocalDateTime.now();

    }
    public boolean fueCompletadoATiempo() {
        if (!estadoCompletado || fechaFinalizacion == null) {
            return false; // No se puede saber si no se ha completado
        }
        return fechaFinalizacion.isBefore(tiempoLimite);
    }
    public String getNombre() {
        return nombre;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    public LocalDateTime getTiempoLimite() {
        return tiempoLimite;
    }

    @Override
    public String toString() {
        return "Quehacer{" +
                "nombre='" + nombre + '\'' +
                ", estadoCompletado=" + estadoCompletado +
                ", tiempoLimite=" + tiempoLimite +
                ", fechaFinalizacion=" + fechaFinalizacion +
                ", dificultad=" + dificultad +
                '}';
    }
}
