package service;

import model.Incentivo;
import model.MiembroHogar;
import model.Quehacer;
import model.EstadoQuehacer;
import model.Dificultad;
import model.Liga;
import org.junit.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * Unit test that exercises the internal crearRecompensa logic of IncentivoService
 * via reflection to avoid touching DAOs / JPA in the test.
 */
public class IncentivoServiceCrearRecompensaTest {

    @Test
    public void crearRecompensa_increases_points_and_updates_liga() throws Exception {
        // Preparación
        IncentivoService svc = new IncentivoService();
        MiembroHogar miembro = new MiembroHogar("Dora", 28);
        miembro.setPuntos(55); // justo debajo del umbral de PLATA
        miembro.setLiga(Liga.BRONCE);

        Quehacer q = new Quehacer("tarea", LocalDateTime.of(2023, 1, 1, 12, 0).plusHours(1), Dificultad.MEDIO);
        q.setMiembroHogar(miembro);
        q.setFechaFinalizacion(LocalDateTime.of(2023, 1, 1, 12, 0));
        q.setEstado(EstadoQuehacer.COMPLETADO);

        Incentivo incentivo = new Incentivo();

        // Invocar método privado crearRecompensa por reflexión
        Method m = IncentivoService.class.getDeclaredMethod("crearRecompensa", Incentivo.class, MiembroHogar.class, Quehacer.class);
        m.setAccessible(true);
        m.invoke(svc, incentivo, miembro, q);

        // Verificaciones: puntos aumentaron y liga cambió a PLATA
        assertEquals(75, miembro.getPuntos());
        assertEquals(Liga.PLATA, miembro.getLiga());

        // Verificar que el incentivo fue rellenado
        assertEquals(Incentivo.PUNTOS_MEDIO, incentivo.getPuntos());
        assertEquals("Completado a tiempo: " + q.getNombre(), incentivo.getDescripcion());
    }
}
