package service;

import org.junit.Before;
import org.junit.Test;
import repository.AchievementRepository;
import service.LogroService;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LogroServiceMockTest {

    private AchievementRepository achievementRepository;
    private LogroService service;
    private final Long miembroId = 30L;

    @Before
    public void setUp() {
        achievementRepository = mock(AchievementRepository.class);
        service = new LogroService(achievementRepository);
    }

    @Test
    public void dado_RachaAlcanzaHito_Cuando_MiembroYaTieneLogro_Entonces_NoOtorgaLogroDuplicado() {
        // Simular que ya tiene el logro de 3
        when(achievementRepository.tieneLogro(miembroId, LogroService.LOGRO_RACHA_3)).thenReturn(true);

        Optional<String> msg3 = service.verificarYAsignarLogroRacha(miembroId, 3);
        assertFalse("No debe otorgar logro ni mensaje si ya lo tiene", msg3.isPresent());
        verify(achievementRepository).tieneLogro(miembroId, LogroService.LOGRO_RACHA_3);
        verify(achievementRepository, never()).guardarLogro(miembroId, LogroService.LOGRO_RACHA_3);

        // Simular que ya tiene el logro de 7
        when(achievementRepository.tieneLogro(miembroId, LogroService.LOGRO_RACHA_7)).thenReturn(true);
        Optional<String> msg7 = service.verificarYAsignarLogroRacha(miembroId, 7);
        assertFalse("No debe otorgar logro de 7 ni mensaje si ya lo tiene", msg7.isPresent());
        verify(achievementRepository).tieneLogro(miembroId, LogroService.LOGRO_RACHA_7);
        verify(achievementRepository, never()).guardarLogro(miembroId, LogroService.LOGRO_RACHA_7);

        verifyNoMoreInteractions(achievementRepository);
    }
}