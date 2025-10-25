package service;

import model.Liga;
import model.MiembroHogar;
import repository.AchievementRepository;

public class LigaService {
    // Constantes extraídas
    private static final int BONIFICACION_POR_ASCENSO = 50;
    private static final String PREFIJO_LOGRO_ASCENSO = "AscensoA";
    private static final int PUNTOS_PLATA = 500;
    private static final int PUNTOS_ORO = 1500;
    private final AchievementRepository achievementRepository;

    // Constructor sin parámetros para Test unitario y Test con parámetros
    public LigaService() {
        this(null);
    }

    // Constructor con inyección de dependencia para Test con mock)
    public LigaService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public void actualizarLiga(MiembroHogar miembro) {
        miembro.setLiga(calcularLigaPorPuntos(miembro.getPuntos()));
    }
    private static Liga calcularLigaPorPuntos(int puntos) {
        if (puntos >= PUNTOS_ORO) return Liga.ORO;
        if (puntos >= PUNTOS_PLATA) return Liga.PLATA;
        return Liga.BRONCE;
    }
    public void actualizarPuntosYLiga(MiembroHogar miembro, int puntosGanados) {
        Liga ligaAnterior = miembro.getLiga();
        
        // Actualizar puntos
        miembro.setPuntos(miembro.getPuntos() + puntosGanados);

        // Actualizar liga según nuevos puntos
        actualizarLiga(miembro);
        Liga ligaNueva = miembro.getLiga();

        //Si hubo ascenso, intentar dar bonificación
        if (esAscenso(ligaAnterior, ligaNueva)) {
            aplicarBonificacionPorAscenso(miembro, ligaNueva);
        }
    }
    private static boolean esAscenso(Liga anterior, Liga nueva) {
        // Substitute algorithm: rely on enum order
        return nueva.ordinal() > anterior.ordinal();
    }
    //Aplica bonificación SOLO si NO tiene el logro
    private void aplicarBonificacionPorAscenso(MiembroHogar miembro, Liga ligaNueva) {
        // Si no hay repositorio, no hacer nada
        if (achievementRepository == null) {
            return;
        }

        String logroId = PREFIJO_LOGRO_ASCENSO + ligaNueva;

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