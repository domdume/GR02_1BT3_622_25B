package service;

import model.Liga;
import model.MiembroHogar;
import repository.AchievementRepository;

public class LigaService {
    // Constantes extraídas
    private static final int BONIFICACION_POR_ASCENSO = 50;
    private static final String PREFIJO_LOGRO_ASCENSO = "AscensoA";

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
            miembro.setLiga(Liga.ORO);
        } else if (puntos >= 500) {
            miembro.setLiga(Liga.PLATA);
        } else {
            miembro.setLiga(Liga.BRONCE);
        }
    }

    public void actualizarPuntosYLiga(MiembroHogar miembro, int puntosGanados) {
        // Actualizar puntos
        miembro.setPuntos(miembro.getPuntos() + puntosGanados);

        // Actualizar liga según nuevos puntos
        actualizarLiga(miembro);

        //Si hubo ascenso, intentar dar bonificación
        if (detectarAscenso(miembro.getLiga(), miembro.getLiga())) {
            aplicarBonificacionPorAscenso(miembro, miembro.getLiga());
        }
    }

    //Detecta si hubo ascenso
    private boolean detectarAscenso(Liga ligaAnterior, Liga ligaNueva) {
        int nivelAnterior = obtenerNivelLiga(ligaAnterior);
        int nivelNuevo = obtenerNivelLiga(ligaNueva);
        return nivelNuevo > nivelAnterior;
    }

    //Convierte liga a número
    private int obtenerNivelLiga(Liga liga) {
        switch (liga) {
            case BRONCE: return 1;
            case PLATA: return 2;
            case ORO: return 3;
            default: return 0;
        }
    }

    //Aplica bonificación SOLO si NO tiene el logro
    private void aplicarBonificacionPorAscenso(MiembroHogar miembro, Liga ligaNueva) {
        // Si no hay repositorio, no hacer nada
        if (achievementRepository == null) {
            return;
        }

        String logroId = "AscensoA" + ligaNueva;

        // Verificar si YA tiene el logro
        boolean yaTieneLogro = achievementRepository.tieneLogro(miembro.getId(), logroId);

        if (!yaTieneLogro) {
            otorgarBonificacionYGuardarLogro(miembro, logroId);
        }
    }

    /**
     * Extraído: aplica la bonificación y persiste el logro.
     */
    private void otorgarBonificacionYGuardarLogro(MiembroHogar miembro, String logroId) {
        miembro.setPuntos(miembro.getPuntos() + BONIFICACION_POR_ASCENSO);
        achievementRepository.guardarLogro(miembro.getId(), logroId);
    }

}