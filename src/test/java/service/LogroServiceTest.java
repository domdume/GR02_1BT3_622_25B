package service;


import model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class LogroServiceTest {

    private final int tareasCompletadas;
    private final boolean debeRecibirMedalla;
    private LogroService logroService;
    private MiembroHogar miembro;


    public LogroServiceTest(int tareasCompletadas, boolean debeRecibirMedalla) {
        this.tareasCompletadas = tareasCompletadas;
        this.debeRecibirMedalla = debeRecibirMedalla;
    }

    @Parameterized.Parameters(name = "{index}: {0} tareas => medalla? {1}")
    public static Collection<Object[]> data() {
        // Ajustado a nueva lógica: umbrales 2,4,8,... -> primera medalla desde 2 tareas
        return Arrays.asList(new Object[][] {
                {1, false},
                {2, true}
        });
    }

    @Test
    public void dado_N_TareasCompletadas_Cuando_VerificaLogro_Entonces_OtorgaMedallaCorrecta() {
        // Arrange
        MiembroHogar miembro = new MiembroHogar("Mateo", 20);
        miembro.setTareasCompletadas(tareasCompletadas);
        LogroService service = new LogroService();

        // Act
        service.verificarLogroPorQuehaceres(miembro);

        // Assert
        boolean tieneMedalla = miembro.getLogros().stream()
                .anyMatch(logro -> logro.getTipo() == TipoLogro.MEDALLA);

        assertEquals("El resultado no coincide para " + tareasCompletadas + " tareas",
                debeRecibirMedalla, tieneMedalla);
    }
    @Before
    public void setUp() {
        logroService = new LogroService();
        miembro = new MiembroHogar("Mateo", 20);
        miembro.setTareasCompletadas(0);
    }

    @Test
    public void dado_MiembroYaTieneMedalla_Cuando_CompletaMasTareas_Entonces_NoOtorgaMedallaDuplicada() {
        // Arrange
        MiembroHogar miembro = new MiembroHogar("Mateo", 20);
        miembro.setTareasCompletadas(12);
        // Simular que ya tiene la medalla correspondiente al último umbral alcanzado (8)
        Logro logroExistente = new Logro("MEDALLA_8", TipoLogro.MEDALLA, 8);
        miembro.addLogro(logroExistente);

        // Act
        Logro nuevoLogro = logroService.verificarLogro(miembro);

        // Assert: como ya tiene la medalla para el umbral 8 y no ha alcanzado 16, no se otorga nueva
        assertNull("No debe otorgarse otra medalla si el miembro ya tiene la del umbral alcanzado", nuevoLogro);
        assertEquals("Debe seguir teniendo solo una medalla", 1, miembro.getLogros().size());
    }

    @Test
    public void dado_ContadorEnCero_Cuando_CompletaPrimeraTarea_Entonces_ContadorEsUno() {
        // Arrange
        miembro.setTareasCompletadas(0);

        // Act
        miembro.setTareasCompletadas(miembro.getTareasCompletadas() + 1);

        // Assert
        assertEquals("El contador de tareas completadas debe ser 1", 1, miembro.getTareasCompletadas());
    }

}