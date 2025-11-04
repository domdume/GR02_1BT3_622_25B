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

        // Siempre verificar el logro más básico al completar una tarea
        achievementRepository.tieneLogro(miembroId, "TAREAS_5");
    }

    public TipoMedalla verificarLogroPorTareas(Long miembroId, int tareasCompletadas) {
        if (miembroId == null || tareasCompletadas < 0) return TipoMedalla.NINGUNA;

        TipoMedalla nivelMasAlto = TipoMedalla.NINGUNA;

        // Verificar y otorgar logros en orden ascendente
        if (tareasCompletadas >= 5 && !achievementRepository.tieneLogro(miembroId, "TAREAS_5")) {
            achievementRepository.guardarLogro(miembroId, "TAREAS_5");
        }

        if (tareasCompletadas >= 10 && !achievementRepository.tieneLogro(miembroId, "TAREAS_10")) {
            achievementRepository.guardarLogro(miembroId, "TAREAS_10");
            nivelMasAlto = TipoMedalla.BRONCE;
        }

        if (tareasCompletadas >= 20 && !achievementRepository.tieneLogro(miembroId, "TAREAS_20")) {
            achievementRepository.guardarLogro(miembroId, "TAREAS_20");
            nivelMasAlto = TipoMedalla.PLATA;
        }

        if (tareasCompletadas >= 30 && !achievementRepository.tieneLogro(miembroId, "TAREAS_30")) {
            achievementRepository.guardarLogro(miembroId, "TAREAS_30");
            nivelMasAlto = TipoMedalla.ORO;
        }

        return nivelMasAlto;
    }

    public boolean tieneMedalla(Long miembroId, String logroId) {
        if (miembroId == null || logroId == null) return false;
        return achievementRepository.tieneLogro(miembroId, logroId);
    }
}
