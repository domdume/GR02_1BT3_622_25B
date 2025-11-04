package service;

import repository.AchievementRepository;

import java.util.Objects;
import java.util.Optional;

/**
 * Servicio de dominio responsable de otorgar logros por rachas alcanzadas.
 * No realiza cálculos de racha; solo aplica reglas de otorgamiento
 * dados el miembro y la racha actual.
 */
public class LogroRachaService {

    // Identificadores de logros expuestos para uso en tests/otras capas
    public static final String LOGRO_RACHA_3 = "LOGRO_3";
    public static final String LOGRO_RACHA_7 = "LOGRO_7";

    private final AchievementRepository achievementRepository;

    public LogroRachaService(AchievementRepository achievementRepository) {
        this.achievementRepository = Objects.requireNonNull(achievementRepository, "achievementRepository es requerido");
    }

    /**
     * Verifica y asigna, si corresponde, el logro de racha alcanzado.
     * Regla de precedencia: si racha >= 7, se evalúa primero el logro de 7 días
     * y no se otorga el de 3 días en el mismo evento. Si no aplica 7, se evalúa 3.
     *
     * @param miembroId  id del miembro
     * @param rachaActual racha actual calculada externamente
     * @return mensaje opcional con la notificación a mostrar
     */
    public Optional<String> verificarYAsignar(Long miembroId, int rachaActual) {
        if (miembroId == null) return Optional.empty();
        if (rachaActual >= 7) {
            if (!achievementRepository.tieneLogro(miembroId, LOGRO_RACHA_7)) {
                achievementRepository.guardarLogro(miembroId, LOGRO_RACHA_7);
                return Optional.of(mensajeLogro7());
            }
            // Si ya tenía el de 7, no otorgar el de 3 en este evento
            return Optional.empty();
        }
        if (rachaActual >= 3) {
            if (!achievementRepository.tieneLogro(miembroId, LOGRO_RACHA_3)) {
                achievementRepository.guardarLogro(miembroId, LOGRO_RACHA_3);
                return Optional.of(mensajeLogro3());
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

    public String mensajeLogro3() {
        return "¡Felicidades!, Ha ganado el logro “Racha de 3 días”";
    }

    public String mensajeLogro7() {
        return "¡Increíble! Ha ganado el logro “Racha de 7 días”";
    }
}

