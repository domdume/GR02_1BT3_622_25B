package model;

import jakarta.persistence.*;

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

    // Constructor para JPA
    public Incentivo() {}

    public void aplicar(MiembroHogar miembro, Quehacer quehacerCompletado) {
        if (quehacerCompletado.fueCompletadoATiempo()) {
            this.tipoIncentivo = TipoIncentivo.Positivo;
            int points = switch (quehacerCompletado.getDificultad()) {
                case FACIL -> 10;
                case MEDIO -> 20;
                case DIFICIL -> 30;
            };
            miembro.setPuntos(miembro.getPuntos() + points);
            System.out.println("👍 ¡Felicidades! " + miembro.getNombre() + " terminó '" + quehacerCompletado.getNombre() + "' a tiempo. Puntos añadidos: " + points);
        } else {
            this.tipoIncentivo = TipoIncentivo.Negativo;
            System.out.println("👎 Lástima, " + miembro.getNombre() + " se retrasó con '" + quehacerCompletado.getNombre() + "'. No se otorga recompensa.");
        }
        miembro.addIncentivo(this);
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


    // Métodos estáticos para mover lógica desde servletQuehacer
    public static void otorgarRecompensaPorCompletar(MiembroHogar miembro, Quehacer quehacer) {
        if (miembro != null) {
            miembro.setPuntos(miembro.getPuntos() + 20);
        }
    }

    public static void aplicarPenalizacionPorVencer(MiembroHogar miembro, Quehacer quehacer) {
        if (miembro != null) {
            miembro.setPuntos(Math.max(0, miembro.getPuntos() - 10));
        }
    }

    public static void aplicarIncentivo(MiembroHogar miembro, Quehacer quehacer) {
        Incentivo incentivo = new Incentivo();
        incentivo.aplicar(miembro, quehacer);
    }
}
