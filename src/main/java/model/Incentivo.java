package model;

import java.util.Comparator;

public class Incentivo {
    private static TipoIncentivo tipoIncentivo;

    //    private Quehacer quehacerPenalizado;
//    private Quehacer quehacerExonerado;
//    private Incentivo(Quehacer exonerado, Quehacer penalizado) {
//        this.quehacerExonerado = exonerado;
//        this.quehacerPenalizado = penalizado;
//    }
    public void aplicar(MiembroHogar miembro, Quehacer quehacerCompletado) {
        if (quehacerCompletado.fueCompletadoATiempo()) {
            tipoIncentivo = TipoIncentivo.Positivo;
            // --- Lógica de Incentivo Positivo ---
            System.out.println("👍 ¡Felicidades! " + miembro.getNombre() + " terminó '" + quehacerCompletado.getNombre() + "' a tiempo.");
            miembro.reducirFactorDeCarga();

            // Buscamos la tarea más fácil que le quede para exonerarla como premio
            Quehacer tareaExonerada = miembro.getQuehaceresAsignados().stream()
                    .min(Comparator.comparing(Quehacer::getDificultad))
                    .orElse(null);

            if (tareaExonerada != null) {
                miembro.removerQuehacer(tareaExonerada);
                System.out.println("✨ INCENTIVO: Como premio, se te ha quitado la tarea '" + tareaExonerada.getNombre() + "'.");
            } else {
                System.out.println("✨ INCENTIVO: ¡No tienes más tareas! Tu factor de carga ahora es " + miembro.getFactorDeCarga());
            }


        } else {
            // --- Lógica de Penalización ---
            tipoIncentivo = TipoIncentivo.Negativo;
            System.out.println("👎 Lástima, " + miembro.getNombre() + " se retrasó con '" + quehacerCompletado.getNombre() + "'.");
            miembro.aumentarFactorDeCarga();
            System.out.println(" PENALIZACIÓN: Se te asignarán más tareas en el futuro. Tu factor de carga ahora es " + miembro.getFactorDeCarga());
        }
        miembro.setIncentivo(this);
    }

    public TipoIncentivo getTipo() {
        return tipoIncentivo;
    }

//    @Override
//    public String toString() {
//        if (quehacerExonerado != null) {
//            return "Incentivo Positivo: Se exoneró la tarea '" + quehacerExonerado.getNombre() + "'";
//        }
//        if (quehacerPenalizado != null) {
//            return "Penalización: Se generó la tarea '" + quehacerPenalizado.getNombre() + "'";
//        }
//        return "Incentivo neutral.";
//    }
}