package service;

import model.*;
import org.junit.Test;
import repository.AchievementRepository;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

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


//    @Test
//    public void dado_MiembroEsNuevo_Cuando_AsciendeDeBronceAPlata_Entonces_AsignaEmblemaAprendizConstante() {
//        // Arrange
//        LogroService logroService = new LogroService();
//        MiembroHogar miembro = new MiembroHogar("TestNuevo", 0);
//        miembro.setId(123L);
//
//        Liga ligaAntes = Liga.BRONCE;
//        Liga ligaDespues = Liga.PLATA;
//
//        // ACT
//        // El método solo guarda en el repositorio, no afecta el miembro.
//        logroService.asignarEmblemaAscenso(miembro, ligaAntes, ligaDespues);
//
//        // ASSERT Trivial (No verifica el Logro, solo el Miembro que no cambia)
//        // El Assert original era `assertEquals(true, asignado)` y `assertEquals(true, tieneEmblema)`.
//        // Como el LogroService no devuelve un booleano ni tiene `tieneEmblema`, este Assert es inválido.
//
//        // Si asumes que el miembro debe haber permanecido con un estado interno:
//        assertEquals(0, miembro.getPuntos());
//    }
//
//    // --- Emblema No se Quita: Descenso (Verifica NO-OP del Servicio) ---
//
//    @Test
//    public void dado_MiembroConLogros_Cuando_Desciende_Entonces_EmblemaNoSeQuita() {
//        // Arrange
//        LogroService logroService = new LogroService();
//
//        MiembroHogar miembro = new MiembroHogar("Test", 500);
//        miembro.setId(456L);
//        miembro.setLiga(Liga.PLATA); // Liga inicial
//
//        // Asumimos que el logro "PREVIO" ya existe en la DB (Paso no comprobable)
//
//        // Act: simular un descenso (que LogroService debe ignorar)
//        Liga ligaAntes = Liga.PLATA;
//        Liga ligaDespues = Liga.BRONCE; // Descenso
//
//        logroService.asignarEmblemaAscenso(miembro, ligaAntes, ligaDespues);
//
//        // Assert: El servicio no debe haber hecho NADA si no es ascenso.
//        // La única cosa que podemos verificar con assertEquals es que el estado del miembro NO cambió.
//
//        // Aseguramos que la liga 'después' de la llamada es la BRONCE (si otro servicio la actualizó)
//        // y que el LogroService no intentó revertir la liga (que no es su responsabilidad).
//
//        // La prueba se centra en verificar que la condición de retorno (`ligaDespues.getNivel() <= ligaAntes.getNivel()`)
//        // fue evaluada y, por lo tanto, no se llamó a `guardarLogro`.
//
//        // Si el `MiembroHogar` tuviera un campo `ultimaLigaAsignada`, podríamos verificarlo,
//        // pero la única verificación posible es la del miembro, que es trivial.
//
//        // Adaptación Trivial: Verificar que el nivel de la liga anterior (PLATA) es mayor que la liga después (BRONCE),
//        // lo cual es la condición de entrada para el 'if' del servicio:
//        assertEquals(true, ligaDespues.getNivel() <= ligaAntes.getNivel());
//    }


    private static class AchievementRepositoryFakeInterno implements AchievementRepository {
        // Mapa: ID de MiembroHogar -> Conjunto de IDs de Logros (strings)
        private final Map<Long, Set<String>> logrosAlmacenados = new HashMap<>();

        @Override
        public void guardarLogro(Long miembroId, String logroId) {
            logrosAlmacenados
                    .computeIfAbsent(miembroId, k -> new HashSet<>())
                    .add(logroId);
        }

        @Override
        public int obtenerTareasCompletadas(Long miembroId) {
            return 0;
        }

        @Override
        public void incrementarContadorTareas(Long miembroId) {

        }

        @Override
        public boolean tieneLogro(Long miembroId, String logroId) {
            return false;
        }

        @Override
        public boolean tieneCualquierLogro(Long miembroId) {
            Set<String> logros = logrosAlmacenados.get(miembroId);
            return logros != null && !logros.isEmpty();
        }

        // Método auxiliar para el Assert de las pruebas
        public boolean tieneLogroEspecifico(Long miembroId, String logroId) {
            Set<String> logros = logrosAlmacenados.get(miembroId);
            return logros != null && logros.contains(logroId);
        }
    }

    // --- Primer Ascenso (Equivalente a 'GanaPrimeraLiga' original) ---

    @Test
    public void dado_MiembroEsNuevo_Cuando_Asciende_Entonces_AsignaEmblemaAprendizConstante() {
        // Arrange:
        // 1. Usar el Fake Interno
        AchievementRepositoryFakeInterno achievementRepositoryFake = new AchievementRepositoryFakeInterno();
        // 2. Inyectar el Fake al servicio
        LogroService logroService = new LogroService(achievementRepositoryFake);

        MiembroHogar miembro = new MiembroHogar("TestNuevo", 0);
        miembro.setId(123L);

        Liga ligaAntes = Liga.BRONCE;
        Liga ligaDespues = Liga.PLATA;
        // El miembro es nuevo (el Fake no tiene logros para el ID 123L)

        // Act: Llamar al método de ascenso
        logroService.asignarEmblemaAscenso(miembro, ligaAntes, ligaDespues);

        // Assert:
        // Verificar el estado del Fake Repository. Debe tener el logro de primera vez.
        assertTrue("Debe asignar el emblema APRENDIZ_CONSTANTE al ser la primera insignia.",achievementRepositoryFake.tieneLogroEspecifico(miembro.getId(), "EMBLEMA_APRENDIZ_CONSTANTE"));
        assertFalse("No debe asignar el logro normal de ascenso.",achievementRepositoryFake.tieneLogroEspecifico(miembro.getId(), "EMBLEMA_EXPLORADOR_PERSISTENTE"));
    }

    // --- Emblema No se Quita (Equivalente a 'PuntosBajan' original) ---

    @Test
    public void dado_MiembroConLogros_Cuando_Desciende_Entonces_NoSeAsignaNingunEmblema() {
        // Arrange:
        AchievementRepositoryFakeInterno achievementRepositoryFake = new AchievementRepositoryFakeInterno();
        LogroService logroService = new LogroService(achievementRepositoryFake);

        MiembroHogar miembro = new MiembroHogar("TestExistente", 100);
        miembro.setId(456L);

        // **Simular que ya tiene un logro inicial (para que no se active la lógica de "primer ascenso")**
        achievementRepositoryFake.guardarLogro(miembro.getId(), "EMBLEMA_PREVIO");
        assertTrue("Debe tener el logro inicial para la prueba.",achievementRepositoryFake.tieneLogroEspecifico(miembro.getId(), "EMBLEMA_PREVIO"));

        // Act: simular un descenso o estancamiento (PLATA a BRONCE)
        Liga ligaAntes = Liga.PLATA;
        Liga ligaDespues = Liga.BRONCE; // Descenso, que el LogroService debe ignorar.

        logroService.asignarEmblemaAscenso(miembro, ligaAntes, ligaDespues);

        // Assert:
        // 1. El logro previo debe permanecer.
        assertTrue("El emblema previo no debería quitarse.",achievementRepositoryFake.tieneLogroEspecifico(miembro.getId(), "EMBLEMA_PREVIO"));

        // 2. No se debe haber guardado NINGÚN logro de ascenso, ya que fue un descenso.
        assertFalse("No debe asignar emblemas en el descenso.",achievementRepositoryFake.tieneLogroEspecifico(miembro.getId(), "EMBLEMA_EXPLORADOR_PERSISTENTE"));
        assertFalse("No debe asignar emblemas en el descenso.",achievementRepositoryFake.tieneLogroEspecifico(miembro.getId(), "EMBLEMA_MAESTRO_QUEHACERES"));
    }

}
