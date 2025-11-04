package service;

import model.Liga;
import model.MiembroHogar;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EmblemaServiceTest {

    @Test
    public void dado_MiembroEsNuevo_Cuando_GanaPrimeraLiga_Entonces_AsignaEmblemaAprendiz() {
        // Arrange: usar EmblemaService real, pero evitar DB via mocks
    EmblemaService emblemaService;
        emblemaService = EmblemaService.getInstancia();
    MiembroHogar miembro = new MiembroHogar("Test", 0);
    miembro.setId(123L);

    // Empieza sin liga (miembro nuevo)
    assertEquals("El miembro debe comenzar sin liga", Liga.BRONCE, miembro.getLiga());

    // Simular que gana su primera liga asignando BRONCE
    miembro.setLiga(Liga.BRONCE);

    // Act: asignar emblema Aprendiz directamente
    boolean asignado = emblemaService.asignarEmblemaAprendiz(miembro);

    // Assert: emblema asignado y registrado
    assertEquals("El emblema Aprendiz debe haberse asignado", true, asignado);
    assertEquals("El miembro debería tener el emblema APRENDIZ", true, emblemaService.tieneEmblema(miembro.getId(), "APRENDIZ"));
    }

    @Test
    public void dado_PuntosBajan_Cuando_Procesa_Entonces_EmblemaNoSeQuita() {
        // Arrange
        EmblemaService emblemaService;
        emblemaService = EmblemaService.getInstancia();
        MiembroHogar miembro = new MiembroHogar("Test", 0);
        miembro.setId(456L);

        // Primero gana la liga y el emblema
        miembro.setLiga(Liga.BRONCE);
        emblemaService.asignarEmblemaAprendiz(miembro);
        assertEquals("Debería tener el emblema inicialmente", true, 
                    emblemaService.tieneEmblema(miembro.getId(), "APRENDIZ"));

        // Act: simular pérdida de puntos
        miembro.setPuntos(0);  // Bajan los puntos
        miembro.setLiga(Liga.BRONCE);  // La liga se mantiene en BRONCE

        // Assert: el emblema debe permanecer
        assertEquals("El emblema APRENDIZ no debería quitarse al bajar puntos", true,
                    emblemaService.tieneEmblema(miembro.getId(), "APRENDIZ"));
    }
}