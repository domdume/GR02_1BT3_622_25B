package model;

import java.time.LocalDate;
import java.util.List;

public class CalculadoraRacha {
    public int calcularRacha(List<LocalDate> historialTareas) {
        if (historialTareas == null || historialTareas.isEmpty()) {
            return 0;
        }
        LocalDate today = LocalDate.now();
        return historialTareas.contains(today) ? 1 : 0;
    }
}
