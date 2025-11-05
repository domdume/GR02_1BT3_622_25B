package service;

import model.Dificultad;
import model.EstadoQuehacer;
import model.MiembroHogar;
import model.Quehacer;
import org.junit.Test;
import java.time.LocalDateTime;

public class EmblemaServiceMockTest {

    @Test
    // ---------- Mock test: verificar que model.Incentivo.aplicarIncentivo invoca IncentivoService
    public void dado_AscensoDeLiga_Cuando_ActualizaPuntos_Entonces_IncentivoServiceEsLlamado() {
        // Reemplazamos la factory para devolver un stub y verificar la llamada sin usar Mockito
        final java.util.concurrent.atomic.AtomicBoolean called = new java.util.concurrent.atomic.AtomicBoolean(false);
        IncentivoService stubSvc = new IncentivoService() {
            @Override
            public void aplicarIncentivo(MiembroHogar miembro, Quehacer quehacer) {
                called.set(true);
            }
        };
        java.util.function.Supplier<IncentivoService> previousFactory = IncentivoService.factory;
        try {
            IncentivoService.setFactory(() -> stubSvc);

            MiembroHogar miembro = new MiembroHogar("Ana", 25);
            miembro.setPuntos(55);

            LocalDateTime base = LocalDateTime.of(2023, 1, 1, 12, 0);
            LocalDateTime limite = base.plusHours(1);
            Quehacer quehacer = new Quehacer("Lavar platos", limite, Dificultad.MEDIO);
            quehacer.setFechaFinalizacion(base);
            quehacer.setEstado(EstadoQuehacer.COMPLETADO);

            model.Incentivo.aplicarIncentivo(miembro, quehacer);

            // Verificamos que el stub fue invocado
            org.junit.Assert.assertTrue("IncentivoService should have been called", called.get());
        } finally {
            IncentivoService.setFactory(previousFactory);
        }
    }

    // T2: test de interacción — verifica que aplicarIncentivo lleva a usar IncentivoService
    @Test
    public void dado_MiembroCon55Puntos_Cuando_AplicaQuehacerCompletado_Entonces_IncentivoServiceEsConstruidoYSuMetodoInvocado() {
        // Usamos un stub concreto en lugar de Mockito.mock para evitar problemas de instrumentación
        final java.util.concurrent.atomic.AtomicBoolean called = new java.util.concurrent.atomic.AtomicBoolean(false);
        IncentivoService stubSvc = new IncentivoService() {
            @Override
            public void aplicarIncentivo(MiembroHogar miembro, Quehacer quehacer) {
                called.set(true);
            }
        };
        java.util.function.Supplier<IncentivoService> previousFactory = IncentivoService.factory;
        try {
            IncentivoService.setFactory(() -> stubSvc);

            // Dado: un miembro cerca del umbral de PLATA
            MiembroHogar miembro = new MiembroHogar("Ana", 25);
            miembro.setPuntos(55);

            // Cuando: creamos un quehacer completado a tiempo (fecha fija)
            LocalDateTime base = LocalDateTime.of(2023, 1, 1, 12, 0);
            LocalDateTime limite = base.plusHours(1);
            Quehacer quehacer = new Quehacer("Lavar platos", limite, Dificultad.MEDIO);
            quehacer.setFechaFinalizacion(base);
            quehacer.setEstado(EstadoQuehacer.COMPLETADO);

            // Acción: llamar al facade que internamente crea IncentivoService mediante la factory
            model.Incentivo.aplicarIncentivo(miembro, quehacer);

            // Entonces: el stub fue invocado
            org.junit.Assert.assertTrue("IncentivoService should have been called", called.get());
        } finally {
            IncentivoService.setFactory(previousFactory);
        }
    }
}
