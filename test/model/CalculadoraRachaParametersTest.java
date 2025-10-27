package model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
@RunWith(Parameterized.class)
public class CalculadoraRachaParametersTest {

    private CalculadoraRacha calculadora;

    private static LocalDate today = LocalDate.now();
    private static LocalDate yesterday = today.minusDays(1);
    private static LocalDate twoDaysAgo = today.minusDays(2);
    private static LocalDate fourDaysAgo = today.minusDays(4);

    private List<LocalDate> taskHistory;
    private int expectedStreak;
    public CalculadoraRachaParametersTest(List<LocalDate> taskHistory, int expectedStreak) {
        this.taskHistory = taskHistory;
        this.expectedStreak = expectedStreak;
    }

    @Before
    public void setUp() {
        calculadora = new CalculadoraRacha();
    }

    @Parameters
    public static Collection<Object[]> parameters() {
        List<Object[]> objects = new ArrayList<>();

        // Caso 0: [Hoy, Ayer, Anteayer] -> Racha 3
        objects.add(new Object[]{List.of(today, yesterday, twoDaysAgo), 3});

        // Caso 1: [Hoy, Ayer, Hoy, Ayer, Ayer] -> Racha 2 (Normalización)
        objects.add(new Object[]{List.of(today, yesterday, today, yesterday, yesterday), 2});

        // Caso 2: [Ayer, Anteayer] -> Racha 2 (La racha de ayer cuenta)
        objects.add(new Object[]{List.of(yesterday, twoDaysAgo), 2});

        // Caso 3: [Ayer] -> Racha 1 (La racha de ayer cuenta)
        objects.add(new Object[]{List.of(yesterday), 1});

        // Caso 4: [Hoy, Ayer, Anteayer, 4-Dias-Atras] -> Racha 3 (Rotura después de 3)
        objects.add(new Object[]{List.of(today, yesterday, twoDaysAgo, fourDaysAgo), 3});

        return objects;
    }
    @Test
    public void dado_UnHistorialDeTareasComplejo_Cuando_CalculaRacha_Entonces_ElResultadoEsElEsperado() {
        int racha = calculadora.calcularRacha(this.taskHistory);

        // Entonces
        assertEquals(expectedStreak, racha);
    }
}