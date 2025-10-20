package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServicioLigaTest {

    @Test
    public void dado_miembroEnBronce_cuando_ganaPuntosExactamente_entonces_subeANivelPlata() {
        // Configurar el miembro en nivel BRONCE
        MiembroHogar miembro = new MiembroHogar("Juan", 25);
        miembro.setLiga(Liga.BRONCE);

        ServicioLiga servicioLiga = new ServicioLiga();
        // Acción: ganar puntos suficientes para subir a PLATA
        servicioLiga.actualizarPuntos(miembro, 1000); // Asumiendo que 1000 puntos son necesarios para subir a PLATA

        // Verificación: el nivel del miembro debe ser PLATA
        assertEquals(Liga.PLATA, miembro.getLiga());
    }
    @Test
    public void dado_MiembroEnOro_cuando_pierdeSuficientespuntos_entonces_desciendeCobre(){
        // Configurar el miembro en nivel ORO
        MiembroHogar miembro = new MiembroHogar("Ana", 30);
        miembro.setLiga(Liga.ORO);
        miembro.setPuntos(1600);

        ServicioLiga servicioLiga = new ServicioLiga();
        // Acción: perder puntos suficientes para descender a BRONCE
        servicioLiga.removerPuntos(miembro, 1200);
        assertEquals(Liga.BRONCE, miembro.getLiga());
    }
    @Test
    public void dado_MiembroEnBronce_cuando_pierdePuntos_entonces_permaneceEnBronce(){
        // Configurar el miembro en nivel BRONCE
        MiembroHogar miembro = new MiembroHogar("Luis", 20);
        miembro.setLiga(Liga.BRONCE);
        miembro.setPuntos(50);

        ServicioLiga servicioLiga = new ServicioLiga();
        // Acción: perder puntos pero permanecer en BRONCE
        servicioLiga.removerPuntos(miembro, 30);
        assertEquals(20, miembro.getPuntos());
        assertEquals(Liga.BRONCE, miembro.getLiga());
    }
    @Test
    public void dado_MiembroConPocosPuntos_cuando_pierdeMasDeLoQueTiene_entonces_puntajeQuedaEnCero(){
        // Configurar el miembro en nivel BRONCE
        MiembroHogar miembro = new MiembroHogar("Alan", 22);
        miembro.setLiga(Liga.BRONCE);
        miembro.setPuntos(30);

        ServicioLiga servicioLiga = new ServicioLiga();
        // Acción: perder puntos pero permanecer en BRONCE
        servicioLiga.removerPuntos(miembro, 50);
        assertEquals(0, miembro.getPuntos());
        assertEquals(Liga.BRONCE, miembro.getLiga());
    }





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