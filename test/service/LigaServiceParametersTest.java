package service;

import model.Liga;
import model.MiembroHogar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(value = Parameterized.class)
public class LigaServiceParametersTest {

    private int puntosIniciales, puntosAPerder;
    private Liga ligaEsperada;

    @Parameterized.Parameters
    public static Iterable<Object[]> parameters() {
        List<Object[]> objects = new ArrayList<Object[]>();
        objects.add(new Object[]{510, 20, Liga.BRONCE});
        objects.add(new Object[]{550, 20, Liga.PLATA});
        objects.add(new Object[]{1550, 100, Liga.PLATA});
        return objects;
    }

    // Constructor que recibe los parámetros
    public LigaServiceParametersTest(int puntosIniciales, int puntosAPerder, Liga ligaEsperada) {
        this.puntosIniciales = puntosIniciales;
        this.puntosAPerder = puntosAPerder;
        this.ligaEsperada = ligaEsperada;
    }

    @Test
    public void dadoUsuarioConPuntosVariables_cuandoPierdePuntosBajoUmbral_entoncesDesciendeInmediatamente() {
        //Crear usuario con puntos iniciales
        MiembroHogar usuario = new MiembroHogar("TestUser", 25);
        usuario.setPuntos(puntosIniciales);

        LigaService ligaService = new LigaService();
        ligaService.actualizarLiga(usuario);

        //Hacer que pierda puntos
        ligaService.actualizarPuntosYLiga(usuario, -puntosAPerder);

        //Verificar liga esperada
        assertEquals(ligaEsperada, usuario.getLiga());

        //Verificar puntos exactos
        int puntosEsperados = puntosIniciales - puntosAPerder;
        assertEquals(puntosEsperados, usuario.getPuntos());
    }
}