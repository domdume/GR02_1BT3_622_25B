package service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import model.MiembroHogar;
import model.Liga;
import repository.AchievementRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para LogroService usando JUnit 4 y Mockito.
 */
public class LogroServiceMockitoTest {

    // 1. Reemplaza @ExtendWith(MockitoExtension.class)
    //    por la regla @Rule de JUnit 4 y Mockito.
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    // 2. Objeto a probar: Se inicializa y se inyectan los mocks (@Mock) automáticamente.
    @InjectMocks
    private LogroService logroService;

    // 3. Dependencia simulada (Mock)
    @Mock
    private AchievementRepository achievementRepository;

    private MiembroHogar miembroConLogros;

    // 4. Reemplaza @BeforeEach por @Before
    @Before
    public void setUp() {
        miembroConLogros = new MiembroHogar("TestUser", 500);
        miembroConLogros.setId(200L);
    }

    @Test
    public void dado_MiembroConLogros_Cuando_AsciendeDeBronceAPlata_Entonces_AsignaEmblemaExploradorPersistente() {
        // Arrange
        Long miembroId = miembroConLogros.getId();

        // Simular que el miembro ya tiene logros (para que NO se active la lógica de "APRENDIZ_CONSTANTE")
        when(achievementRepository.tieneCualquierLogro(miembroId)).thenReturn(true);

        // Act
        // Ascenso de BRONCE a PLATA
        logroService.asignarEmblemaAscenso(miembroConLogros, Liga.BRONCE, Liga.PLATA);

        // Assert

        // Capturar el argumento pasado al método guardarLogro
        ArgumentCaptor<String> logroIdCaptor = ArgumentCaptor.forClass(String.class);

        // Verificar que el método guardarLogro fue llamado EXACTAMENTE una vez
        verify(achievementRepository, times(1)).guardarLogro(eq(miembroId), logroIdCaptor.capture());

        // Usar assertEquals para confirmar que el logro capturado es el esperado
        String logroAsignado = logroIdCaptor.getValue();
        assertEquals("Debe asignar el emblema correcto para el ascenso de BRONCE a PLATA.",
                "EMBLEMA_EXPLORADOR_PERSISTENTE", logroAsignado);

        // Verificar que el logro de primer ascenso NO fue llamado
        verify(achievementRepository, never()).guardarLogro(anyLong(), eq("EMBLEMA_APRENDIZ_CONSTANTE"));
    }
}