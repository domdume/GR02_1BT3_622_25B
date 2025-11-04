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
        // Configurar que no tiene ningún logro inicialmente
        when(mockRepository.tieneLogro(miembroId, "TAREAS_5")).thenReturn(false);
        when(mockRepository.tieneLogro(miembroId, "TAREAS_10")).thenReturn(false);

        // Act & Assert - Caso 5 tareas
        TipoMedalla medallaCincoTareas = logroService.verificarLogroPorTareas(miembroId, 5);
        assertEquals("Con 5 tareas completadas debería obtener medalla NINGUNA",
            TipoMedalla.NINGUNA, medallaCincoTareas);

        // Simular que ya tiene el logro de 5 tareas para la siguiente verificación
        when(mockRepository.tieneLogro(miembroId, "TAREAS_5")).thenReturn(true);

        // Act & Assert - Caso 10 tareas
        TipoMedalla medallaDiezTareas = logroService.verificarLogroPorTareas(miembroId, 10);
        assertEquals("Con 10 tareas completadas debería obtener medalla BRONCE",
            TipoMedalla.BRONCE, medallaDiezTareas);

        // Verify
        verify(mockRepository, times(1)).guardarLogro(miembroId, "TAREAS_5");
        verify(mockRepository, times(1)).guardarLogro(miembroId, "TAREAS_10");
        verify(mockRepository, times(2)).tieneLogro(miembroId, "TAREAS_5");
        verify(mockRepository, times(1)).tieneLogro(miembroId, "TAREAS_10");
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    public void dado_MiembroYaTieneMedalla_Cuando_CompletaMasTareas_Entonces_NoOtorgaMedallaDuplicada() {
        // Arrange
        Long miembroId = 1L;
        when(mockRepository.tieneLogro(miembroId, "TAREAS_5")).thenReturn(true);

        // Act
        TipoMedalla medalla = logroService.verificarLogroPorTareas(miembroId, 6);

        // Assert
        assertEquals("Con 6 tareas y ya teniendo la medalla TAREAS_5 debería mantener NINGUNA",
            TipoMedalla.NINGUNA, medalla);
        verify(mockRepository, times(1)).tieneLogro(miembroId, "TAREAS_5");
        verify(mockRepository, never()).guardarLogro(eq(miembroId), eq("TAREAS_5"));
        verifyNoMoreInteractions(mockRepository);
    }

    @Test
    public void dado_ContadorEnCero_Cuando_CompletaPrimeraTarea_Entonces_ContadorEsUno() {
        // Arrange
        Long miembroId = 1L;
        when(mockRepository.obtenerTareasCompletadas(miembroId)).thenReturn(0);

        // Act
        logroService.registrarTareaCompletada(miembroId);

        // Assert
        verify(mockRepository).incrementarContadorTareas(miembroId);
        verify(mockRepository).obtenerTareasCompletadas(miembroId);
        verifyNoMoreInteractions(mockRepository);
    }
}
