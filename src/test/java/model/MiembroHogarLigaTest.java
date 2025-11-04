package model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MiembroHogarLigaTest {

    @Test
    public void constructor_sets_default_liga_bronce_and_zero_points() {
        MiembroHogar m = new MiembroHogar("Juan", 30);
        assertEquals(Liga.BRONCE, m.getLiga());
        assertEquals(0, m.getPuntos());
    }

    @Test
    public void set_and_get_puntos_and_liga() {
        MiembroHogar m = new MiembroHogar("Ana", 25);
        m.setPuntos(70);
        m.setLiga(Liga.PLATA);
        assertEquals(70, m.getPuntos());
        assertEquals(Liga.PLATA, m.getLiga());
    }
}
