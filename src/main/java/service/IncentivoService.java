package service;

import dao.IncentivoDAO;
import model.Incentivo;
import model.MiembroHogar;
import model.Quehacer;
import model.TipoIncentivo;

public class IncentivoService {
    private final IncentivoDAO incentivoDAO;

    public IncentivoService() {
        this.incentivoDAO = new IncentivoDAO();
    }

    // Constructor para testing con mock
    IncentivoService(IncentivoDAO incentivoDAO) {
        this.incentivoDAO = incentivoDAO;
    }

    public void aplicarIncentivo(MiembroHogar miembro, Quehacer quehacer) {
        if (miembro == null || quehacer == null) {
            throw new IllegalArgumentException("Miembro y quehacer no pueden ser nulos");
        }

        Incentivo incentivo = new Incentivo();
        
        if (quehacer.fueCompletadoATiempo()) {
            crearRecompensa(incentivo, miembro, quehacer);
        } else {
            crearPenalizacion(incentivo, miembro, quehacer);
        }

        // Establecer la relación bidireccional
        incentivo.setMiembroHogar(miembro);
        incentivo.setQuehacer(quehacer);
        miembro.anadirIncentivo(incentivo);

        // Persistir el incentivo
        incentivoDAO.create(incentivo);
    }

    private void crearRecompensa(Incentivo incentivo, MiembroHogar miembro, Quehacer quehacer) {
        incentivo.setTipoIncentivo(TipoIncentivo.RECOMPENSA);
        int puntos = switch (quehacer.getDificultad()) {
            case FACIL -> Incentivo.PUNTOS_FACIL;
            case MEDIO -> Incentivo.PUNTOS_MEDIO;
            case DIFICIL -> Incentivo.PUNTOS_DIFICIL;
        };
        incentivo.setPuntos(puntos);
        incentivo.setDescripcion("Completado a tiempo: " + quehacer.getNombre());
        miembro.setPuntos(miembro.getPuntos() + puntos);
        System.out.println("👍 ¡Felicidades! " + miembro.getNombre() + " terminó '" + 
            quehacer.getNombre() + "' a tiempo. Puntos añadidos: " + puntos);
    }

    private void crearPenalizacion(Incentivo incentivo, MiembroHogar miembro, Quehacer quehacer) {
        incentivo.setTipoIncentivo(TipoIncentivo.PENALIZACION);
        incentivo.setPuntos(-Incentivo.PENALIZACION);
        incentivo.setDescripcion("No completado a tiempo: " + quehacer.getNombre());
        miembro.setPuntos(Math.max(0, miembro.getPuntos() - Incentivo.PENALIZACION));
        System.out.println("👎 Lástima, " + miembro.getNombre() + " se retrasó con '" + 
            quehacer.getNombre() + "'. Penalización: -" + Incentivo.PENALIZACION + " puntos.");
    }
}