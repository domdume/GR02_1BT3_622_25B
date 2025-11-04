package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Quehacer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoQuehacer estado; // Ajuste para que coincida con la base de datos
    private LocalDateTime tiempoLimite;
    private LocalDateTime fechaFinalizacion;
    @Enumerated(EnumType.STRING)
    private Dificultad dificultad; // Campo agregado según diagrama UML

    @ManyToOne
    @JoinColumn(name = "miembro_id", nullable = false)
    private MiembroHogar miembroHogar; // Ajuste para que coincida con la base de datos

    @Transient
    private int puntosEnEseMomento; // Campo calculado para mostrar puntos progresivos

    @Transient
    private boolean vencido; // bandera calculada en servidor para evitar llamadas a métodos EL

    private String recompensa;

    // Constructor vacío para JPA
    public Quehacer() {
        this.estado = EstadoQuehacer.PENDIENTE;
    }

    public Quehacer(String nombre, LocalDateTime tiempoLimite, Dificultad dificultad) {
        this.nombre = nombre;
        this.tiempoLimite = tiempoLimite;
        this.dificultad = dificultad;
        this.estado = EstadoQuehacer.PENDIENTE;
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

    public EstadoQuehacer getEstado() {
        return estado;
    }

    public void setEstado(EstadoQuehacer estado) {
        this.estado = estado;
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

    // Lógica de negocio
    
    // Métodos según diagrama UML
    public boolean isCompletado() {
        return this.estado == EstadoQuehacer.COMPLETADO;
    }

    public void marcarCompletado() {
        this.estado = EstadoQuehacer.COMPLETADO;
        // Solo establecer fecha de finalización si no fue proporcionada anteriormente
        if (this.fechaFinalizacion == null) {
            this.fechaFinalizacion = LocalDateTime.now();
        }
    }

    public boolean fueCompletadoATiempo() {
        if (estado != EstadoQuehacer.COMPLETADO || fechaFinalizacion == null|| tiempoLimite == null) {
            return false;
        }
        return !fechaFinalizacion.toLocalDate().isAfter(tiempoLimite.toLocalDate());
    }

    public boolean estaVencido() {
        return estado == EstadoQuehacer.VENCIDO || (estado == EstadoQuehacer.PENDIENTE && tiempoLimite != null && LocalDateTime.now().isAfter(tiempoLimite));
    }

    // Método de compatibilidad para verificar si está finalizado (no pendiente)
    public boolean isEstadoFinalizado() {
        return estado != EstadoQuehacer.PENDIENTE;
    }


    public int getPuntosEnEseMomento() {
        return puntosEnEseMomento;
    }

    public void setPuntosEnEseMomento(int puntosEnEseMomento) {
        this.puntosEnEseMomento = puntosEnEseMomento;
    }

    public boolean isVencido() {
        return vencido;
    }

    public void setVencido(boolean vencido) {
        this.vencido = vencido;
    }

    public String getTiempoLimiteFmt() {
        if (tiempoLimite == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return tiempoLimite.format(formatter);
    }

    public String getFechaFinalizacionFmt() {
        if (fechaFinalizacion == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return fechaFinalizacion.format(formatter);
    }

    @Override
    public String toString() {
        return "Quehacer{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", estado=" + estado +
                '}';
    }
}
