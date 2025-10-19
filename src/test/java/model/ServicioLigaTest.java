package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServicioLigaTest {

    private ServicioLiga servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioLiga();
    }


    @Test
    void given_miembroEnBronce_when_anadirVariosPuntos_then_ascenderVariasLigas() {
        // Puntos iniciales por debajo del umbral de Plata (ej: Bronce)
        MiembroHogar miembro = new MiembroHogar("Pedro",45);
        miembro.setLiga(Liga.BRONCE);

        // Ganar suficientes puntos para superar el umbral de Oro (ej: 100 + 1400 = 1500)
        servicio.actualizarPuntos(miembro, 1500);

        assertEquals(Liga.ORO, miembro.getLiga(), "El miembro debería ascender a ORO.");
    }

    @Test
    void given_miembroEnOro_when_sumarPuntos_then_mantenerseEnOro() {
        // Miembro ya en Oro con puntos por encima del umbral
        MiembroHogar miembro = new MiembroHogar("Pedro",15);
        miembro.setLiga(Liga.BRONCE);
        // Gana más puntos
        servicio.actualizarPuntos(miembro, 2000);

        assertEquals(Liga.ORO, miembro.getLiga(),"El miembro en Oro debe permanecer en ORO después de ganar más puntos.");
    }

}