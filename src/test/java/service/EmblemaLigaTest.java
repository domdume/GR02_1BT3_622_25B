package service;

import model.Dificultad;
import model.EstadoQuehacer;
import model.Incentivo;
import model.MiembroHogar;
import model.Quehacer;
import org.junit.Test;
import repository.AchievementRepository;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests agrupados para HU3 (T1 y T2). Cada test usa el formato
 * dado_X_Cuando_Y_Entonces_Z para clarificar el contrato.
 *
 * Nota: algunos tests son de especificación y fallarán hasta que
 * las clases/firmas solicitadas estén implementadas (TDD: rojo->verde).
 */
public class EmblemaLigaTest {

    // T1: estructura de emblemas
    @Test
    public void dado_RequisitosDominio_Cuando_VerificamosLaEstructura_Entonces_DeberiaExistirLogroTipoYRelacionEnMiembro() {
        // Dado: requisitos del dominio
        // Cuando: comprobamos existencia de la entidad Logro
        try {
            Class.forName("model.Logro");
        } catch (ClassNotFoundException e) {
            // Entonces: fallar con mensaje claro
            fail("Falta la clase model.Logro. Implementa la entidad Logro según T1.");
        }

        // Cuando: comprobamos enum TipoLogro
        try {
            Class<?> tipo = Class.forName("model.TipoLogro");
            assertTrue("model.TipoLogro debe ser un enum", tipo.isEnum());
        } catch (ClassNotFoundException e) {
            fail("Falta el enum model.TipoLogro. Define TipoLogro con valores MEDALLA, LOGRO_RACHA, EMBLEMA.");
        }

        // Cuando: comprobamos que MiembroHogar expone getLogros()
        try {
            Class<?> miembro = Class.forName("model.MiembroHogar");
            Method m = miembro.getMethod("getLogros");
            assertNotNull("getLogros() debe existir en model.MiembroHogar", m);
        } catch (ClassNotFoundException e) {
            fail("No se encontró model.MiembroHogar: revisa que la clase exista en package model.");
        } catch (NoSuchMethodException e) {
            fail("Falta el método getLogros() en model.MiembroHogar. Añade una colección/relación para logros.");
        }
    }

    // T2: especificación de integración mínima
    @Test
    public void dado_SeQuiereAsignarEmblema_Cuando_InspeccionamosIncentivoService_Entonces_DeberiaAceptarLogroServicePorConstructor() {
        // Dado: espera de existencia de LogroService
        try {
            Class.forName("service.LogroService");
        } catch (ClassNotFoundException e) {
            fail("Falta la clase service.LogroService. Implementa la clase que gestione la persistencia de Logro.");
            return;
        }

        // Cuando: buscamos un constructor en IncentivoService que acepte LogroService
        try {
            Class<?> incentivoClass = Class.forName("service.IncentivoService");
            Class<?> logroServiceClass = Class.forName("service.LogroService");
            boolean found = false;
            for (var c : incentivoClass.getConstructors()) {
                for (var p : c.getParameterTypes()) {
                    if (p.equals(logroServiceClass)) { found = true; break; }
                }
                if (found) break;
            }
            // Entonces: debe existir tal constructor
            if (!found) fail("service.IncentivoService debe proporcionar un constructor que acepte service.LogroService (inyección para pruebas).");
        } catch (ClassNotFoundException e) {
            fail("No se encontró service.IncentivoService. Revisa que exista en el paquete service.");
        }
    }

    @Test
    public void dado_LogroService_Cuando_SeInspecciona_Entonces_DeberiaTenerAsignarEmblemaAscensoConFirmasCorrectas() {
        try {
            Class<?> logroCls = Class.forName("service.LogroService");
            Method m = logroCls.getMethod("asignarEmblemaAscenso", Class.forName("model.MiembroHogar"), Class.forName("model.Liga"), Class.forName("model.Liga"));
            assertNotNull(m);
        } catch (ClassNotFoundException e) {
            fail("Para verificar asignarEmblemaAscenso: falta alguna de las clases esperadas (LogroService/MiembroHogar/Liga). Implementalas para cumplir T2.");
        } catch (NoSuchMethodException e) {
            fail("service.LogroService debe exponer asignarEmblemaAscenso(MiembroHogar, Liga, Liga). Añade ese método para T2.");
        }
    }

    // T2: unidad — invocar crearRecompensa por reflexión para verificar la lógica de puntos/ligas
    @Test
    public void dado_Miembro55Puntos_Cuando_CrearRecompensa_Entonces_PuntosYSuLigaSeActualizan() throws Exception {
        // Dado
        var svc = new IncentivoService();
        MiembroHogar miembro = new MiembroHogar("Dora", 28);
        miembro.setPuntos(55);

        LocalDateTime base = LocalDateTime.of(2023, 1, 1, 12, 0);
        Quehacer q = new Quehacer("tarea", base.plusHours(1), Dificultad.MEDIO);
        q.setMiembroHogar(miembro);
        q.setFechaFinalizacion(base);
        q.setEstado(EstadoQuehacer.COMPLETADO);

        Incentivo incentivo = new Incentivo();

        // Cuando: invocamos el método privado
        Method m = IncentivoService.class.getDeclaredMethod("crearRecompensa", Incentivo.class, MiembroHogar.class, Quehacer.class);
        m.setAccessible(true);
        m.invoke(svc, incentivo, miembro, q);

        // Entonces: los puntos y la liga se actualizan y el incentivo queda rellenado
        assertEquals(75, miembro.getPuntos());
        assertEquals(model.Liga.PLATA, miembro.getLiga());
        assertEquals(Incentivo.PUNTOS_MEDIO, incentivo.getPuntos());
        assertEquals("Completado a tiempo: " + q.getNombre(), incentivo.getDescripcion());
    }
}
