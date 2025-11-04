package service;

import org.junit.Before;
import org.junit.Test;
import repository.AchievementRepository;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Verifica que si el miembro ya tenía el logro de 3 días y alcanza racha 7,
 * se otorga el de 7 (y no se re-otorga el de 3).
 */
public class LogroRachaServicePrecedenciaTest {

    private AchievementRepository achievementRepository;
    private LogroRachaService service;
    private final Long miembroId = 40L;

    @Before
    public void setUp() {
        achievementRepository = mock(AchievementRepository.class);
        service = new LogroRachaService(achievementRepository);
    }

    @Test
    public void dado_TeniaLogro3_Cuando_AlcanzaRacha7_Entonces_OtorgaLogro7() {
        when(achievementRepository.tieneLogro(miembroId, LogroRachaService.LOGRO_RACHA_7)).thenReturn(false);
        // Podría tener ya el de 3, no interfiere con el de 7
        when(achievementRepository.tieneLogro(miembroId, LogroRachaService.LOGRO_RACHA_3)).thenReturn(true);

        Optional<String> msg = service.verificarYAsignar(miembroId, 7);
        assertTrue(msg.isPresent());
        assertEquals(service.mensajeLogro7(), msg.get());

        verify(achievementRepository).tieneLogro(miembroId, LogroRachaService.LOGRO_RACHA_7);
        verify(achievementRepository).guardarLogro(miembroId, LogroRachaService.LOGRO_RACHA_7);
        // No intenta guardar el de 3 nuevamente
        verify(achievementRepository, never()).guardarLogro(miembroId, LogroRachaService.LOGRO_RACHA_3);
        verifyNoMoreInteractions(achievementRepository);
    }
}


