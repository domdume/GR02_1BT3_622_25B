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

        // Determinar fecha de inicio (hoy o ayer). Si ninguna aplica, racha = 0
        LocalDate inicio = determinarFechaInicio(dias);
        if (inicio == null) {
            return 0;
        }

        // Delegar el conteo de la racha al método específico
        return contarRachaDesde(dias, inicio, isFrozen);
    }

    private LocalDate determinarFechaInicio(Set<LocalDate> dias) {
        LocalDate hoy = LocalDate.now();
        boolean tieneHoy = dias.contains(hoy);
        LocalDate ayer = hoy.minusDays(1);
        boolean tieneAyer = dias.contains(ayer);

        if (tieneHoy) return hoy;
        if (tieneAyer) return ayer;
        return null; // Ni hoy ni ayer
    }

    private int contarRachaDesde(Set<LocalDate> dias, LocalDate inicio, boolean isFrozen) {
        int racha = 0;
        boolean gapUsado = false;
        LocalDate cursor = inicio;

        // Contar días consecutivos hacia atrás desde el día de inicio
        while (true) {
            boolean tieneDia = dias.contains(cursor);
            if (tieneDia) {
                racha++;
                cursor = cursor.minusDays(1);
                continue;
            }

            // Día faltante
            boolean puedeSaltar = isFrozen && !gapUsado;
            if (puedeSaltar) {
                gapUsado = true; // consumir un gap de 1 día
                cursor = cursor.minusDays(1); // saltar el día faltante
                // no incrementa racha por el día faltante
                continue;
            }

            break; // rotura definitiva
        }

        return racha;
    }
}