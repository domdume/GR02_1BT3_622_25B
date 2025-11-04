package service;

import model.TipoMedalla;
import repository.AchievementRepository;

public class LogroService {
    private final AchievementRepository achievementRepository;

    public LogroService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public TipoMedalla verificarLogroPorTareas(Long miembroId, int tareasCompletadas) {
        TipoMedalla nivelMasAlto = TipoMedalla.NINGUNA;

        // Verificar logro de 5 tareas
        if (tareasCompletadas >= 5 && !achievementRepository.tieneLogro(miembroId, "TAREAS_5")) {
            achievementRepository.guardarLogro(miembroId, "TAREAS_5");
        }

        // Verificar logro de 10 tareas
        if (tareasCompletadas >= 10 && !achievementRepository.tieneLogro(miembroId, "TAREAS_10")) {
            achievementRepository.guardarLogro(miembroId, "TAREAS_10");
            nivelMasAlto = TipoMedalla.BRONCE;
        }

        return nivelMasAlto;
    }
}
