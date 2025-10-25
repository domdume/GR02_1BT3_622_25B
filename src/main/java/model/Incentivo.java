package model;

import jakarta.persistence.*;
import dao.IncentivoDAO;
import java.time.LocalDateTime;

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

    private static final int PUNTOS_FACIL = 10;
    private static final int PUNTOS_MEDIO = 20;
    private static final int PUNTOS_DIFICIL = 30;
    private static final int PENALIZACION = 5;

    public void aplicar(MiembroHogar miembro, Quehacer quehacerCompletado) {
        if (miembro == null || quehacerCompletado == null) {
            throw new IllegalArgumentException("Miembro y quehacer no pueden ser nulos");
        }

        if (quehacerCompletado.fueCompletadoATiempo()) {
            this.tipoIncentivo = TipoIncentivo.RECOMPENSA;
            int points = switch (quehacerCompletado.getDificultad()) {
                case FACIL -> PUNTOS_FACIL;
                case MEDIO -> PUNTOS_MEDIO;
                case DIFICIL -> PUNTOS_DIFICIL;
            };
            this.puntos = points;
            this.descripcion = "Completado a tiempo: " + quehacerCompletado.getNombre();
            miembro.setPuntos(miembro.getPuntos() + points);
            System.out.println("👍 ¡Felicidades! " + miembro.getNombre() + " terminó '" + quehacerCompletado.getNombre() + "' a tiempo. Puntos añadidos: " + points);
        } else {
            this.tipoIncentivo = TipoIncentivo.PENALIZACION;
            this.puntos = -PENALIZACION;
            this.descripcion = "No completado a tiempo: " + quehacerCompletado.getNombre();
            miembro.setPuntos(Math.max(0, miembro.getPuntos() - PENALIZACION));
            System.out.println("👎 Lástima, " + miembro.getNombre() + " se retrasó con '" + quehacerCompletado.getNombre() + "'. Penalización: -" + PENALIZACION + " puntos.");
        }

        // Establecer la relación bidireccional
        this.setMiembroHogar(miembro);
        this.setQuehacer(quehacerCompletado);
        miembro.anadirIncentivo(this);

        // Persistir el incentivo directamente usando DAO
        IncentivoDAO incentivoDAO = new IncentivoDAO();
        incentivoDAO.create(this);
    }

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


    // Métodos estáticos para mover lógica desde servletQuehacer
    public static void otorgarRecompensaPorCompletar(MiembroHogar miembro, Quehacer quehacer) {
        if (miembro != null) {
            int puntosRecompensa = 20;
            miembro.setPuntos(miembro.getPuntos() + puntosRecompensa);

            // Crear incentivo y persistirlo
            Incentivo incentivo = new Incentivo(
                TipoIncentivo.RECOMPENSA, 
                puntosRecompensa, 
                "Quehacer completado: " + quehacer.getNombre(), 
                miembro, 
                quehacer
            );

            IncentivoDAO incentivoDAO = new IncentivoDAO();
            incentivoDAO.create(incentivo);

            System.out.println("[INCENTIVO] Recompensa otorgada: +" + puntosRecompensa + " puntos para " + miembro.getNombre());
        }
    }

    public static void aplicarPenalizacionPorVencer(MiembroHogar miembro, Quehacer quehacer) {
        if (miembro != null) {
            int puntosPenalizacion = -10;
            miembro.setPuntos(Math.max(0, miembro.getPuntos() - 10));

            // Crear incentivo negativo y persistirlo
            Incentivo incentivo = new Incentivo(
                TipoIncentivo.PENALIZACION, 
                puntosPenalizacion, 
                "Quehacer vencido: " + quehacer.getNombre(), 
                miembro, 
                quehacer
            );

            IncentivoDAO incentivoDAO = new IncentivoDAO();
            incentivoDAO.create(incentivo);

            System.out.println("[INCENTIVO] Penalización aplicada: -10 puntos para " + miembro.getNombre());
        }
    }

    public static void aplicarIncentivo(MiembroHogar miembro, Quehacer quehacer) {
        Incentivo incentivo = new Incentivo();
        incentivo.aplicar(miembro, quehacer);
    }
}
