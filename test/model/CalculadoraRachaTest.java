package model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CalculadoraRachaTest {
    private CalculadoraRacha calculadora;
    @Before
    public void setUp() {
        // Inicializar el calculador antes de cada test
        calculadora = new CalculadoraRacha();
    }
    @Test
    public void dado_HistorialDeTareasVacio_Cuando_CalculaRacha_Entonces_RachaEsCero() {
        List<LocalDate> historialTareas = new ArrayList<>();
        int racha = calculadora.calcularRacha(historialTareas);
        assertEquals(0, racha);
    }

    @Test
    public void dado_HistorialConUnaTareaHoy_Cuando_CalculaRacha_Entonces_RachaEsUno() {
        LocalDate date = LocalDate.now();
        List<LocalDate> historialTareas = List.of(date);
        int racha = calculadora.calcularRacha(historialTareas);
        assertEquals(1, racha);
    }

    @Test
    public void dado_HistorialConTareasHoyYAyer_Cuando_CalculaRacha_Entonces_RachaEsDos() {
        LocalDate hoy = LocalDate.now();
        LocalDate ayer = hoy.minusDays(1);
        List<LocalDate> historialTareas = List.of(hoy, ayer);
        int racha = calculadora.calcularRacha(historialTareas);
        assertEquals(2, racha);
    }

    @Test
    public void dado_HistorialCon5TareasHoy_Cuando_CalculaRacha_Entonces_RachaEsUno() {
        LocalDate hoy = LocalDate.now();
        List<LocalDate> historialTareas = List.of(hoy, hoy, hoy, hoy, hoy);
        int racha = calculadora.calcularRacha(historialTareas);
        assertEquals(1, racha);
    }
}

