package model;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CalculadoraRachaFrozenTest {

    @Test
    public void dado_RachaRotaYMiembroNoCongelado_Cuando_CalculaRacha_Entonces_RachaSeRompe() {
        // hoy y anteayer (falta ayer) -> sin frozen: cuenta solo hoy => 1
        LocalDate today = LocalDate.now();
        LocalDate anteayer = today.minusDays(2);
        List<LocalDate> historial = Arrays.asList(today, anteayer);

        CalculadoraRacha calc = new CalculadoraRacha();
        int racha = calc.calcularRacha(historial, false);
        assertEquals(1, racha);
    }
}