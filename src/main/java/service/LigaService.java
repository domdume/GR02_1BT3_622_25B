package service;

import model.Liga;
import model.MiembroHogar;
import repository.AchievementRepository;

/**
 * Servicio unificado para manejar puntos y ligas.
 * Esta clase sustituye a la anterior `ServicioLiga` y a la versión que manejaba
 * logros/bonificaciones; aquí solo se actualiza el puntaje y la liga del miembro.
 *
 * Se mantienen métodos compatibilidad con tests anteriores: `actualizarLiga` y
 * `actualizarPuntosYLiga`, además de un constructor que acepta un
 * `AchievementRepository` (no se usa en esta implementación).
 */
public class LigaService {

    // Umbrales de liga (ajustados según requerimientos)
    private static final int PLATA_UMBRAL = 60;
    private static final int ORO_UMBRAL = 100;

    // Repositorio opcional para compatibilidad con tests/mocks (no usado aquí)
    private final AchievementRepository achievementRepository;

    public LigaService() {
        this.achievementRepository = null;
    }

    // Constructor para permitir inyección de mock en tests; no se utiliza en la lógica
    public LigaService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    /**
     * Método de compatibilidad: recalcula la liga del miembro según sus puntos actuales.
     */
    public void actualizarLiga(MiembroHogar miembro) {
        if (miembro == null) return;
        actualizarLigaSegunPuntos(miembro);
    }

    /**
     * Añade o remueve puntos y actualiza la liga. Firma esperada por tests.
     * Puntos negativo significa remoción.
     */
    public void actualizarPuntosYLiga(MiembroHogar miembro, int puntos) {
        // Reutilizar la lógica que ya maneja negativos
        actualizarPuntos(miembro, puntos);
    }

    /**
     * Valida si un miembro es válido para actualización de puntos
     */
    private boolean esActualizacionValida(MiembroHogar miembro, int puntos) {
        return miembro != null && puntos != 0;
    }

    /**
     * Añade puntos al miembro y actualiza su liga según los umbrales.
     * No realiza ninguna acción relacionada con incentivos o logros.
     */
    public void actualizarPuntos(MiembroHogar miembro, int puntos) {
        if (!esActualizacionValida(miembro, puntos)) return;

        if (puntos < 0) {
            // Si se pasa valor negativo, delegar a removerPuntos para comportamiento consistente
            removerPuntos(miembro, -puntos);
            return;
        }
        int nuevos = miembro.getPuntos() + puntos;
        miembro.setPuntos(nuevos);
        actualizarLigaSegunPuntos(miembro);
    }

    /**
     * Remueve puntos del miembro (sin llegar a negativos) y actualiza su liga.
     */
    public void removerPuntos(MiembroHogar miembro, int puntosARemover) {
        if (!esActualizacionValida(miembro, puntosARemover)) return;

        int nuevos = Math.max(0, miembro.getPuntos() - puntosARemover);
        miembro.setPuntos(nuevos);
        actualizarLigaSegunPuntos(miembro);
    }

    /**
     * Determina la liga correspondiente según los puntos
     */
    private Liga determinarLigaPorPuntos(int puntos) {
        if (puntos >= ORO_UMBRAL) return Liga.ORO;
        if (puntos >= PLATA_UMBRAL) return Liga.PLATA;
        return Liga.BRONCE;
    }

    /**
     * Actualiza la liga del miembro utilizando los umbrales definidos.
     */
    private void actualizarLigaSegunPuntos(MiembroHogar miembro) {
        miembro.setLiga(determinarLigaPorPuntos(miembro.getPuntos()));
    }

}
