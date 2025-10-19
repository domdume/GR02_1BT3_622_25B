package model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ServicioNotificacionesTest {

    @Test
    public void dado_miembroNuevo_cuando_ganaPuntosGradualmente_entonces_subeDeLigaCorrectamente() {
        // Configurar el miembro en nivel BRONCE
        MiembroHogar miembro = new MiembroHogar("Carlos", 28);
        miembro.setLiga(Liga.BRONCE);
        miembro.setPuntos(0);

        ServicioLiga servicioLiga = new ServicioLiga();

        // Gana 800 puntos, aún no debe subir de liga
        servicioLiga.actualizarPuntos(miembro, 400);
        assertEquals(400, miembro.getPuntos());
        assertEquals(Liga.BRONCE, miembro.getLiga());

        // Gana 300 puntos más → total 1100, debería subir a PLATA
        servicioLiga.actualizarPuntos(miembro, 300);
        assertEquals(700, miembro.getPuntos());
        assertEquals(Liga.PLATA, miembro.getLiga());

        // Gana otros 1000 puntos → total 2100, debería subir a ORO
        servicioLiga.actualizarPuntos(miembro, 1000);
        assertEquals(1700, miembro.getPuntos());
        assertEquals(Liga.ORO, miembro.getLiga());
    }

    @Test
    public void dado_unMiembro_cuando_asciendeDeLiga_entonces_seLeEnviaUnaNotificacionFelicitandolo(){
        MiembroHogar miembro = new MiembroHogar("Carlos", 28);
        miembro.setLiga(Liga.BRONCE);
        miembro.setPuntos(0);

        ServicioLiga servicioLiga = new ServicioLiga();
        servicioLiga.actualizarPuntos(miembro, 600);

        Observador observador = Mockito.mock(Observador.class);

        RegistroQuehacer registroQuehacer = new RegistroQuehacer();
        registroQuehacer.suscribir(observador);
        registroQuehacer.notificar("Felicidades has ascendido de liga...");

        verify(observador, times(1)).actualizar("Felicidades has ascendido de liga...");
    }

    @Test
    public void dado_unPuntaje_cuando_esExactoAlUmbralInferior_entonces_elMiembroDesciendeDeLiga(){
        MiembroHogar miembro = new MiembroHogar("Carlos", 28);
        miembro.setLiga(Liga.ORO);
        miembro.setPuntos(1700);

        ServicioLiga servicioLiga = new ServicioLiga();
        servicioLiga.removerPuntos(miembro, 200);
        assertEquals(Liga.PLATA, miembro.getLiga());

        servicioLiga.removerPuntos(miembro, 1000);
        assertEquals(Liga.BRONCE, miembro.getLiga());
    }
}
