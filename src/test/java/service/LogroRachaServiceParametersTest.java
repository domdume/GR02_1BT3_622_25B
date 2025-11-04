package service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import repository.AchievementRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class LogroRachaServiceParametersTest {

    @Parameterized.Parameters(name = "racha={0} -> logro={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {3, LogroRachaService.LOGRO_RACHA_3},
                {7, LogroRachaService.LOGRO_RACHA_7}
        });
    }

    private final int racha;
    private final String esperadoLogroId;

    public LogroRachaServiceParametersTest(int racha, String esperadoLogroId) {
        this.racha = racha;
        this.esperadoLogroId = esperadoLogroId;
    }

    private AchievementRepository achievementRepository;
    private LogroRachaService service;
    private final Long miembroId = 10L;

    @Before
    public void setUp() {
        achievementRepository = Mockito.mock(AchievementRepository.class);
        // Por defecto, no tiene ningún logro
        when(achievementRepository.tieneLogro(anyLong(), anyString())).thenReturn(false);
        service = new LogroRachaService(achievementRepository);
    }

    @Test
    public void dado_RachaAlcanzaHito_Cuando_VerificaLogro_Entonces_OtorgaLogroCorrecto() {
        Optional<String> mensaje = service.verificarYAsignar(miembroId, racha);

        assertTrue("Se debe retornar un mensaje de logro", mensaje.isPresent());
        if (esperadoLogroId.equals(LogroRachaService.LOGRO_RACHA_3)) {
            assertEquals(service.mensajeLogro3(), mensaje.get());
            verify(achievementRepository).tieneLogro(miembroId, LogroRachaService.LOGRO_RACHA_3);
            verify(achievementRepository).guardarLogro(miembroId, LogroRachaService.LOGRO_RACHA_3);
            verify(achievementRepository, never()).guardarLogro(eq(miembroId), eq(LogroRachaService.LOGRO_RACHA_7));
        } else {
            assertEquals(service.mensajeLogro7(), mensaje.get());
            verify(achievementRepository).tieneLogro(miembroId, LogroRachaService.LOGRO_RACHA_7);
            verify(achievementRepository).guardarLogro(miembroId, LogroRachaService.LOGRO_RACHA_7);
            verify(achievementRepository, never()).guardarLogro(eq(miembroId), eq(LogroRachaService.LOGRO_RACHA_3));
        }
        verifyNoMoreInteractions(achievementRepository);
    }
}
