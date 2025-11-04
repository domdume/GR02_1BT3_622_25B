package service;

import model.TipoMedalla;
import org.junit.Before;
import org.junit.Test;
import repository.AchievementRepository;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class LogroServiceTest {
    private LogroService logroService;
    private AchievementRepository mockRepository;

    @Before
    public void setUp() {
        mockRepository = mock(AchievementRepository.class);
        logroService = new LogroService(mockRepository);
    }

    @Test
    public void dado_N_TareasCompletadas_Cuando_VerificaLogro_Entonces_OtorgaMedallaCorrecta() {
        // Arrange
        Long miembroId = 1L;

        // Act & Assert - Caso 5 tareas
        TipoMedalla medallaCincoTareas = logroService.verificarLogroPorTareas(miembroId, 5);
        assertEquals("Con 5 tareas completadas debería obtener medalla NINGUNA",
            TipoMedalla.NINGUNA, medallaCincoTareas);

        // Act & Assert - Caso 10 tareas
        TipoMedalla medallaDiezTareas = logroService.verificarLogroPorTareas(miembroId, 10);
        assertEquals("Con 10 tareas completadas debería obtener medalla BRONCE",
            TipoMedalla.BRONCE, medallaDiezTareas);

        // Verify
        verify(mockRepository, times(1)).guardarLogro(miembroId, "TAREAS_5");
        verify(mockRepository, times(1)).guardarLogro(miembroId, "TAREAS_10");
    }
}
