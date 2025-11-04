package service;

import model.Logro;
import repository.AchievementRepository;
import repository.JpaAchievementRepository;
import model.MiembroHogar;
import model.Liga;
import model.TipoLogro;

import java.util.Optional;

/**
 * Servicio para gestionar la lógica de logros.
 * Se proporciona un constructor por defecto que crea un repository JPA
 * para que los métodos no lancen NullPointerException si se usa el ctor vacío.
 */
public class LogroService {
    public static final String LOGRO_RACHA_3 = "LOGRO_3";
    public static final String LOGRO_RACHA_7 = "LOGRO_7";
    private final AchievementRepository achievementRepository;

    public LogroService() {
        // Inyectar implementación por defecto basada en JPA
        this.achievementRepository = new JpaAchievementRepository();
    }

    public LogroService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public void asignarEmblemaAscenso(MiembroHogar miembro, Liga ligaAntes, Liga ligaDespues) {
        if (miembro == null || ligaAntes == null || ligaDespues == null) return;
        try {
            // Solo actuar si hubo un ascenso
            if (ligaDespues.getNivel() <= ligaAntes.getNivel()) return;

            Long miembroId = miembro.getId();
            if (miembroId == null) return;

            // Si es la primera insignia del usuario, asignar Aprendiz Constante
            if (!achievementRepository.tieneCualquierLogro(miembroId)) {
                achievementRepository.guardarLogro(miembroId, "EMBLEMA_APRENDIZ_CONSTANTE");
                return; // primera insignia asignada, no asignar la habitual
            }

            // Mapear ascensos a identificadores de emblema
            if (ligaAntes == Liga.BRONCE && ligaDespues == Liga.PLATA) {
                // Bronce -> Plata
                String logroId = "EMBLEMA_EXPLORADOR_PERSISTENTE";
                achievementRepository.guardarLogro(miembroId, logroId);
            } else if (ligaAntes == Liga.PLATA && ligaDespues == Liga.ORO) {
                // Plata -> Oro
                String logroId = "EMBLEMA_MAESTRO_QUEHACERES";
                achievementRepository.guardarLogro(miembroId, logroId);
            }
        } catch (Exception ex) {
            System.out.println("[ERROR] asignarEmblemaAscenso: " + ex.getMessage());
        }
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
    public Optional<String> verificarYAsignarLogroRacha(Long miembroId, int rachaActual) {
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

    // Variante que también devuelve el tipo de logro ganado
    public Optional<AchievementNotification> verificarYAsignarLogroRachaConTipo(Long miembroId, int rachaActual) {
        if (miembroId == null) return Optional.empty();
        if (rachaActual >= 7) {
            if (!achievementRepository.tieneLogro(miembroId, LOGRO_RACHA_7)) {
                achievementRepository.guardarLogro(miembroId, LOGRO_RACHA_7);
                return Optional.of(new AchievementNotification(LOGRO_RACHA_7, TipoLogro.LOGRO_RACHA, mensajeLogro7()));
            }
            return Optional.empty();
        }
        if (rachaActual >= 3) {
            if (!achievementRepository.tieneLogro(miembroId, LOGRO_RACHA_3)) {
                achievementRepository.guardarLogro(miembroId, LOGRO_RACHA_3);
                return Optional.of(new AchievementNotification(LOGRO_RACHA_3, TipoLogro.LOGRO_RACHA, mensajeLogro3()));
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

    public String mensajeLogro3() {
        return "¡Felicidades!, Ha ganado el logro “Chispazo”";
    }

    public String mensajeLogro7() {
        return "¡Increíble! Ha ganado el logro “Semana Perfecta”";
    }

    public void verificarLogroPorQuehaceres(MiembroHogar miembro) {
        if (miembro == null) {
            throw new IllegalArgumentException("El miembro no puede ser nulo");
        }

        // Verificar si ya tiene una medalla
        boolean yaTieneMedalla = miembro.getLogros().stream()
                .anyMatch(logro -> logro.getTipo() == TipoLogro.MEDALLA);

        // Otorgar medalla si cumple la condición
        if (!yaTieneMedalla && miembro.getTareasCompletadas() >= 10) {
            Logro logro = new Logro();
            logro.setTipoLogro(TipoLogro.MEDALLA);
            miembro.addLogro(logro);
            System.out.println("🏅 Se otorgó una medalla a " + miembro.getNombre());
        } else {
            System.out.println("ℹ️ " + miembro.getNombre() + " aún no cumple los requisitos para una medalla.");
        }


    }

    // DTO simple para notificar tipo y mensaje del logro
    public static class AchievementNotification {
        private final String logroId;
        private final TipoLogro tipo;
        private final String mensaje;

        public AchievementNotification(String logroId, TipoLogro tipo, String mensaje) {
            this.logroId = logroId;
            this.tipo = tipo;
            this.mensaje = mensaje;
        }

        public String getLogroId() { return logroId; }
        public TipoLogro getTipo() { return tipo; }
        public String getMensaje() { return mensaje; }

}}
