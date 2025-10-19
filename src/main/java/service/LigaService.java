package service;

import model.MiembroHogar;
import repository.AchievementRepository;

public class LigaService {
    private AchievementRepository achievementRepository;

    // Constructor sin parámetros para Test unitario y Test con parámetros
    public LigaService() {
        this(null);
    }

    // Constructor con inyección de dependencia para Test con mock)
    public LigaService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public void actualizarLiga(MiembroHogar miembro) {
        int puntos = miembro.getPuntos();

        // Determinar liga según puntos
        if (puntos >= 1500) {
            miembro.setLiga("ORO");
        } else if (puntos >= 500) {
            miembro.setLiga("PLATA");
        } else {
            miembro.setLiga("BRONCE");
        }
    }

    public void actualizarPuntosYLiga(MiembroHogar miembro, int puntosGanados) {
        String ligaAnterior = miembro.getLiga();

        // Actualizar puntos
        int nuevosPuntos = miembro.getPuntos() + puntosGanados;
        miembro.setPuntos(nuevosPuntos);

        // Actualizar liga según nuevos puntos
        actualizarLiga(miembro);
        String ligaNueva = miembro.getLiga();

        //Si hubo ascenso, intentar dar bonificación
        if (detectarAscenso(ligaAnterior, ligaNueva)) {
            aplicarBonificacionPorAscenso(miembro, ligaNueva);
        }
    }

    //Detecta si hubo ascenso
    private boolean detectarAscenso(String ligaAnterior, String ligaNueva) {
        int nivelAnterior = obtenerNivelLiga(ligaAnterior);
        int nivelNuevo = obtenerNivelLiga(ligaNueva);
        return nivelNuevo > nivelAnterior;
    }

    //Convierte liga a número
    private int obtenerNivelLiga(String liga) {
        switch (liga) {
            case "BRONCE": return 1;
            case "PLATA": return 2;
            case "ORO": return 3;
            default: return 0;
        }
    }

    //Aplica bonificación SOLO si NO tiene el logro
    private void aplicarBonificacionPorAscenso(MiembroHogar miembro, String ligaNueva) {
        // Si no hay repositorio, no hacer nada
        if (achievementRepository == null) {
            return;
        }

        String logroId = "AscensoA" + ligaNueva;

        // Verificar si YA tiene el logro
        boolean yaTieneLogro = achievementRepository.tieneLogro(miembro.getId(), logroId);

        if (!yaTieneLogro) {
            // SOLO dar bonificación si NO tiene el logro
            int bonificacion = 50;
            miembro.setPuntos(miembro.getPuntos() + bonificacion);

            // Guardar el logro para que no se repita
            achievementRepository.guardarLogro(miembro.getId(), logroId);
        }
    }

}