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
import static org.mockito.ArgumentMatchers.eq;

public class EmblemaServiceMockTest {

    @Test
    // ---------- Mock test: verificar que model.Incentivo.aplicarIncentivo invoca IncentivoService
    public void dado_AscensoDeLiga_Cuando_ActualizaPuntos_Entonces_IncentivoServiceEsLlamado() {
        try (MockedConstruction<IncentivoService> mocked = Mockito.mockConstruction(IncentivoService.class)) {
            MiembroHogar miembro = new MiembroHogar("Ana", 25);
            miembro.setPuntos(55);

            LocalDateTime base = LocalDateTime.of(2023, 1, 1, 12, 0);
            LocalDateTime limite = base.plusHours(1);
            Quehacer quehacer = new Quehacer("Lavar platos", limite, Dificultad.MEDIO);
            quehacer.setFechaFinalizacion(base);
            quehacer.setEstado(EstadoQuehacer.COMPLETADO);

            model.Incentivo.aplicarIncentivo(miembro, quehacer);

            List<IncentivoService> constructed = mocked.constructed();
            assertEquals(1, constructed.size());
            Mockito.verify(constructed.get(0)).aplicarIncentivo(eq(miembro), eq(quehacer));
        }
    }

    // T2: test de interacción — verifica que aplicarIncentivo lleva a usar IncentivoService
    @Test
    public void dado_MiembroCon55Puntos_Cuando_AplicaQuehacerCompletado_Entonces_IncentivoServiceEsConstruidoYSuMetodoInvocado() {
        try (MockedConstruction<IncentivoService> mocked = Mockito.mockConstruction(IncentivoService.class)) {
            // Dado: un miembro cerca del umbral de PLATA
            MiembroHogar miembro = new MiembroHogar("Ana", 25);
            miembro.setPuntos(55);

            // Cuando: creamos un quehacer completado a tiempo (fecha fija)
            LocalDateTime base = LocalDateTime.of(2023, 1, 1, 12, 0);
            LocalDateTime limite = base.plusHours(1);
            Quehacer quehacer = new Quehacer("Lavar platos", limite, Dificultad.MEDIO);
            quehacer.setFechaFinalizacion(base);
            quehacer.setEstado(EstadoQuehacer.COMPLETADO);

            // Acción: llamar al facade que internamente construye IncentivoService
            model.Incentivo.aplicarIncentivo(miembro, quehacer);

            // Entonces: se construyó exactamente una instancia y se llamó aplicarIncentivo
            List<IncentivoService> constructed = mocked.constructed();
            assertEquals(1, constructed.size());
            Mockito.verify(constructed.get(0)).aplicarIncentivo(eq(miembro), eq(quehacer));
        }
    }
}
