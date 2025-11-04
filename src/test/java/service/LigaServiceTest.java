package service;

import model.MiembroHogar;
import model.Liga;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LigaServiceTest {

    @Test
    public void actualizarPuntos_from55_plus20_becomesPLATA() {
        MiembroHogar m = new MiembroHogar("Ana", 20);
        m.setPuntos(55);
        new LigaService().actualizarPuntos(m, 20);
        assertEquals(75, m.getPuntos());
        assertEquals(Liga.PLATA, m.getLiga());
    }

    @Test
    public void actualizarPuntos_from95_plus10_becomesORO() {
        MiembroHogar m = new MiembroHogar("Beto", 25);
        m.setPuntos(95);
        new LigaService().actualizarPuntos(m, 10);
        assertEquals(105, m.getPuntos());
        assertEquals(Liga.ORO, m.getLiga());
    }

    @Test
    public void removerPuntos_does_not_go_negative_and_updates_liga() {
        MiembroHogar m = new MiembroHogar("Carlos", 30);
        m.setPuntos(10);
        new LigaService().removerPuntos(m, 20);
        assertEquals(0, m.getPuntos());
        assertEquals(Liga.BRONCE, m.getLiga());
    }
}
