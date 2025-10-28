package model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalculadoraRacha {
    public int calcularRacha(List<LocalDate> historialTareas) {
        return calcularRacha(historialTareas, false);
    }

    public int calcularRacha(List<LocalDate> historialTareas, boolean isFrozen) {
        if (historialTareas == null || historialTareas.isEmpty()) {
            return 0;
        }
        Set<LocalDate> dias = new HashSet<>(historialTareas);
        LocalDate inicio = LocalDate.now();
        // Si hoy no hay tareas, permitir que la racha comience en "ayer"
        if (!dias.contains(inicio)) {
            inicio = inicio.minusDays(1);
            if (!dias.contains(inicio)) {
                return 0; // Ni hoy ni ayer: racha 0
            }
        }
        int racha = 0;
        // Contar días consecutivos hacia atrás desde el día de inicio
        boolean gapUsado = false;
        LocalDate cursor = inicio;
        while (true) {
            if (dias.contains(cursor)) {
                racha++;
                cursor = cursor.minusDays(1);
                continue;
            }
            // Día faltante
            if (isFrozen && !gapUsado) {
                gapUsado = true;       // consumir un gap de 1 día
                cursor = cursor.minusDays(1); // saltar el día faltante
                // no incrementa racha por el día faltante
                continue;
            }
            break; // rotura definitiva
        }
        return racha;
    }
}