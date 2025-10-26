package service;

import model.MiembroHogar;
import model.Liga;
import org.junit.Test;
import static org.junit.Assert.*;

public class LigaServiceTest {

    @Test
    public void dado_MiembroConPuntosAltos_Cuando_ActualizaLiga_Entonces_Asciende() {
        LigaService service = new LigaService();
        MiembroHogar m = new MiembroHogar("Ana", 25);
        m.setPuntos(200);
        service.actualizarLiga(m);
        assertTrue(m.getLiga().ordinal() >= Liga.PLATA.ordinal());
    }
}

