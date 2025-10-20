package model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class ServicioLigaParametersTest {
    private ServicioLiga servicio;

    // --- Propiedades de la Clase (Parámetros) ---
    // Deben coincidir con el orden de los datos en el método parameters()
    private int puntosIniciales;
    private Liga ligaInicial;
    private int puntosAñadidos;
    private Liga ligaEsperada;
    // Necesitamos la liga inicial para el caso de permanencia en Plata


    // 2. Constructor: Se llama para cada conjunto de datos
    public ServicioLigaParametersTest(int puntosIniciales, Liga ligaInicial, int puntosAñadidos, Liga ligaEsperada) {
        this.puntosIniciales = puntosIniciales;
        this.ligaInicial = ligaInicial; // Usamos el cuarto parámetro para la liga de inicio
        this.puntosAñadidos = puntosAñadidos;
        this.ligaEsperada = ligaEsperada;
    }

    @Before
    public void setUp() {
        // Inicializar el servicio antes de cada test (una vez por conjunto de datos)
        servicio = new ServicioLiga();
    }

    // 3. Método @Parameters: Proporciona los datos de prueba
    @Parameterized.Parameters()
    public static Collection<Object[]> parameters() {
        List <Object[]> objects = new ArrayList<Object[]>();
                // { Puntos Iniciales, Liga Inicial, Puntos a Añadir, Liga Esperada }

                // Caso 1: (490 puntos + 20 = 510) -> asciende a Plata (> 500).
                objects.add(new Object[]{490, Liga.BRONCE, 20, Liga.PLATA});

                // Caso 2: (600 puntos + 50 = 650) -> permanece en Plata. (Requiere Liga Inicial = PLATA)
        objects.add(new Object[]{600, Liga.PLATA, 50, Liga.PLATA});

                // Caso 3: (450 puntos + 20 = 470) -> permanece en Bronce (no supera 500).
        objects.add(new Object[]{450, Liga.BRONCE, 20, Liga.BRONCE});

                // Caso extra: Borde exacto (500 + 1 = 501) -> asciende a Plata (> 500)
        objects.add(new Object[]{500, Liga.BRONCE, 1, Liga.PLATA});

                // Caso extra: Borde 500 exacto (499 + 1 = 500) -> permanece en Bronce (500 NO es > 500)
        objects.add(new Object[]{499, Liga.BRONCE, 1, Liga.BRONCE});

                // Caso extra: Salto a Oro (1500 + 1 = 1501) -> asciende a Oro (> 1500)
        objects.add(new Object[]{1500, Liga.PLATA, 1, Liga.ORO});
        return objects;
    }

    // 4. Método @Test: Se ejecuta una vez por cada conjunto de parámetros
    @Test
    public void given_miembroEnBronce_when_sumaPuntos_then_subeLigaAPlata() {
        // Usamos los parámetros para inicializar el MiembroHogar
        MiembroHogar miembro = new MiembroHogar("Mario",18);
        miembro.setPuntos(puntosIniciales); // Establecer puntos iniciales
        miembro.setLiga(ligaInicial);
        // Act (Ejecución)
        servicio.actualizarPuntos(miembro, puntosAñadidos);

        // Assert (Verificación)
        assertEquals("deberia ser igual", ligaEsperada, miembro.getLiga());
    }
}