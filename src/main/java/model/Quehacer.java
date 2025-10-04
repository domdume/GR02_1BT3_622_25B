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
}
