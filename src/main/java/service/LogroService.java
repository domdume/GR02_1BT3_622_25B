package service;

import model.TipoMedalla;
import repository.AchievementRepository;

public class LogroService {
    private final AchievementRepository achievementRepository;

    public LogroService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public void registrarTareaCompletada(Long miembroId) {
        if (miembroId == null) return;

        int tareasCompletadas = achievementRepository.obtenerTareasCompletadas(miembroId);
        achievementRepository.incrementarContadorTareas(miembroId);
        verificarLogroPorTareas(miembroId, tareasCompletadas + 1);
    }

    public TipoMedalla verificarLogroPorTareas(Long miembroId, int tareasCompletadas) {
        TipoMedalla nivelMasAlto = TipoMedalla.NINGUNA;

        // Verificar logros en orden ascendente de tareas requeridas
        if (tareasCompletadas >= 5 && !achievementRepository.tieneLogro(miembroId, "TAREAS_5")) {
            achievementRepository.guardarLogro(miembroId, "TAREAS_5");
        }

        // Solo verificar el siguiente nivel si cumple con la cantidad de tareas
        if (tareasCompletadas >= 10) {
            if (!achievementRepository.tieneLogro(miembroId, "TAREAS_10")) {
                achievementRepository.guardarLogro(miembroId, "TAREAS_10");
                nivelMasAlto = TipoMedalla.BRONCE;
            }
        }

        return nivelMasAlto;
    }
}
