package service;

import model.Logro;
import model.TipoMedalla;
import model.TipoLogro;
import repository.AchievementRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LogroService {
    private final AchievementRepository achievementRepository;
    private final List<Logro> logrosDisponibles;
    private final Set<String> logrosGuardados;

    public LogroService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
        this.logrosDisponibles = inicializarLogros();
        this.logrosGuardados = new HashSet<>();
    }

    private List<Logro> inicializarLogros() {
        List<Logro> logros = new ArrayList<>();
        // Logros por tareas completadas (Medallas)
        logros.add(new Logro("TAREAS_5", TipoLogro.MEDALLA, TipoMedalla.NINGUNA, 5));
        logros.add(new Logro("TAREAS_10", TipoLogro.MEDALLA, TipoMedalla.BRONCE, 10));
        logros.add(new Logro("TAREAS_20", TipoLogro.MEDALLA, TipoMedalla.PLATA, 20));
        logros.add(new Logro("TAREAS_30", TipoLogro.MEDALLA, TipoMedalla.ORO, 30));

        // Logros por rachas (Emblemas)
        logros.add(new Logro("STREAK_3", TipoLogro.EMBLEMA, TipoMedalla.NINGUNA, 3));
        logros.add(new Logro("STREAK_7", TipoLogro.EMBLEMA, TipoMedalla.BRONCE, 7));
        logros.add(new Logro("STREAK_14", TipoLogro.EMBLEMA, TipoMedalla.PLATA, 14));
        logros.add(new Logro("STREAK_30", TipoLogro.EMBLEMA, TipoMedalla.ORO, 30));

        return logros;
    }

    public TipoMedalla verificarLogroPorTareas(Long miembroId, int tareasCompletadas) {
        TipoMedalla nivelMasAlto = TipoMedalla.NINGUNA;

        // Primero verificamos el logro de 5 tareas
        if (tareasCompletadas >= 5 && !logrosGuardados.contains("TAREAS_5")) {
            achievementRepository.guardarLogro(miembroId, "TAREAS_5");
            logrosGuardados.add("TAREAS_5");
            nivelMasAlto = TipoMedalla.NINGUNA;
        }

        // Luego verificamos el logro de 10 tareas
        if (tareasCompletadas >= 10 && !logrosGuardados.contains("TAREAS_10")) {
            achievementRepository.guardarLogro(miembroId, "TAREAS_10");
            logrosGuardados.add("TAREAS_10");
            nivelMasAlto = TipoMedalla.BRONCE;
        }

        return nivelMasAlto;
    }

    public TipoMedalla verificarLogroPorRacha(Long miembroId, int diasRacha) {
        TipoMedalla nivelMasAlto = TipoMedalla.NINGUNA;

        // Obtener los logros aplicables ordenados por tareasRequeridas
        List<Logro> logrosAplicables = logrosDisponibles.stream()
            .filter(logro -> logro.getTipoLogro() == TipoLogro.EMBLEMA &&
                           logro.seCumpleConTareas(diasRacha))
            .sorted((l1, l2) -> Integer.compare(l1.getTareasRequeridas(), l2.getTareasRequeridas()))
            .toList();

        // Guardar los logros en orden
        for (Logro logro : logrosAplicables) {
            achievementRepository.guardarLogro(miembroId, logro.getId());
            if (logro.getNivel().ordinal() > nivelMasAlto.ordinal()) {
                nivelMasAlto = logro.getNivel();
            }
        }

        return nivelMasAlto;
    }
}
