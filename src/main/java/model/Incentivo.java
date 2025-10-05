package model;

import jakarta.persistence.*;
import java.util.Comparator;

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
            System.out.println("👍 ¡Felicidades! " + miembro.getNombre() + " terminó '" + quehacerCompletado.getNombre() + "' a tiempo.");
            miembro.reducirFactorDeCarga();

            Quehacer tareaExonerada = miembro.getQuehaceres().stream()
                    .filter(q -> !q.isEstadoCompletado())
                    .min(Comparator.comparing(q -> q.getDificultad().ordinal()))
                    .orElse(null);

            if (tareaExonerada != null) {
                miembro.removerQuehacer(tareaExonerada); // Asumiendo que este método existe y funciona
                System.out.println("✨ INCENTIVO: Como premio, se te ha quitado la tarea '" + tareaExonerada.getNombre() + "'.");
            } else {
                System.out.println("✨ INCENTIVO: ¡No tienes más tareas! Tu factor de carga ahora es " + miembro.getFactorDeCarga());
            }
        } else {
            this.tipoIncentivo = TipoIncentivo.Negativo;
            System.out.println("👎 Lástima, " + miembro.getNombre() + " se retrasó con '" + quehacerCompletado.getNombre() + "'.");
            miembro.aumentarFactorDeCarga();
            System.out.println(" PENALIZACIÓN: Se te asignarán más tareas en el futuro. Tu factor de carga ahora es " + miembro.getFactorDeCarga());
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
}