package service;

import org.junit.Before;
import org.junit.Test;
import repository.AchievementRepository;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LogroRachaServiceBreakTest {

    private AchievementRepository achievementRepository;
    private LogroRachaService service;
    private final Long miembroId = 20L;

    @Before
    public void setUp() {
        achievementRepository = mock(AchievementRepository.class);
        service = new LogroRachaService(achievementRepository);
    }

    @Test
    public void dado_RachaSeRompe_Cuando_VerificaLogro_Entonces_NoOtorgaLogro() {
        // racha 0/1/2 no debe otorgar nada
        for (int racha : new int[]{0,1,2}) {
            Optional<String> msg = service.verificarYAsignar(miembroId, racha);
            assertFalse("No debe haber mensaje para racha="+racha, msg.isPresent());
        }
        verify(achievementRepository, never()).guardarLogro(anyLong(), anyString());
        verify(achievementRepository, never()).tieneLogro(anyLong(), anyString());
        verifyNoMoreInteractions(achievementRepository);
    }
}

