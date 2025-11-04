package service;

import repository.AchievementRepository;
import repository.JpaAchievementRepository;
import model.MiembroHogar;
import model.Liga;

/**
 * Servicio para gestionar la lógica de logros.
 * Se proporciona un constructor por defecto que crea un repository JPA
 * para que los métodos no lancen NullPointerException si se usa el ctor vacío.
 */
public class LogroService {

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
            } else {
                // Otros ascensos no mapeados explícitamente por ahora
            }
        } catch (Exception ex) {
            System.out.println("[ERROR] asignarEmblemaAscenso: " + ex.getMessage());
        }
    }


}
