package service;

import dao.IncentivoDAO;
import dao.MiembroHogarDAO;
import model.Incentivo;
import model.MiembroHogar;
import model.Quehacer;
import model.TipoIncentivo;

public class IncentivoService {
    private final IncentivoDAO incentivoDAO;
    private final LigaService ligaService;

    public IncentivoService() {
        this.incentivoDAO = new IncentivoDAO();
        // Inyectar un AchievementRepository JPA por defecto para persistir logros
        this.ligaService = new LigaService();
    }

    // Constructor para testing con mock
    IncentivoService(IncentivoDAO incentivoDAO) {
        this.incentivoDAO = incentivoDAO;
        this.ligaService = new LigaService();
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

        // Después de persistir, actualizar la liga y procesar bonificación por ascenso
        // LigaService se encargará de consultar y persistir logros si corresponde
        ligaService.actualizarPuntosYLiga(miembro, 0); // ya se sumaron puntos en crearRecompensa/crearPenalizacion

        // Persistir el miembro para que los cambios de puntos y liga se reflejen en la BD
        try {
            new MiembroHogarDAO().update(miembro);
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudo persistir MiembroHogar tras aplicar incentivo: " + e.getMessage());
            e.printStackTrace();
        }
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
        // Penalización equivalente a los puntos que otorgaría la dificultad
        int puntos = switch (quehacer.getDificultad()) {
            case FACIL -> Incentivo.PUNTOS_FACIL;
            case MEDIO -> Incentivo.PUNTOS_MEDIO;
            case DIFICIL -> Incentivo.PUNTOS_DIFICIL;
        };
        incentivo.setPuntos(-puntos);
        incentivo.setDescripcion("No completado a tiempo: " + quehacer.getNombre());
        miembro.setPuntos(Math.max(0, miembro.getPuntos() - puntos));
        System.out.println("👎 Lástima, " + miembro.getNombre() + " se retrasó con '" + quehacer.getNombre() + "'. Penalización: -" + puntos + " puntos.");
    }
}
