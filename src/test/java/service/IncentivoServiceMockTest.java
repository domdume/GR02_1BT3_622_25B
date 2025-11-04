package service;

import model.Dificultad;
import model.EstadoQuehacer;
import model.MiembroHogar;
import model.Quehacer;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IncentivoServiceMockTest {

    @Test
    public void dado_AscensoDeLiga_Cuando_ActualizaPuntos_Entonces_IncentivoServiceEsLlamado() {
        // Mockeamos la construcción de IncentivoService para interceptar la instancia creada
        try (MockedConstruction<IncentivoService> mocked = Mockito.mockConstruction(IncentivoService.class)) {
            // Preparar datos mínimos: miembro y quehacer completado a tiempo que provoque subida de liga
            MiembroHogar miembro = new MiembroHogar("Ana", 25);
            miembro.setPuntos(55); // cercano al umbral de PLATA (60)

            LocalDateTime limite = LocalDateTime.now().plusHours(1);
            Quehacer quehacer = new Quehacer("Lavar platos", limite, Dificultad.MEDIO);
            quehacer.setFechaFinalizacion(limite);
            quehacer.setEstado(EstadoQuehacer.COMPLETADO);

            // Llamada al método que internamente creará una instancia de IncentivoService
            model.Incentivo.aplicarIncentivo(miembro, quehacer);

            // Verificar que se construyó exactamente una instancia y que su método aplicarIncentivo fue invocado
            List<IncentivoService> constructed = mocked.constructed();
            assertEquals("Se esperaba que se construyera una instancia de IncentivoService", 1, constructed.size());
            Mockito.verify(constructed.get(0)).aplicarIncentivo(Mockito.eq(miembro), Mockito.eq(quehacer));
        }
    }
}
