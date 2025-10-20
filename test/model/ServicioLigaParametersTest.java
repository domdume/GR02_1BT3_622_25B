package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.testng.annotations.Test;
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

                // Caso 1: (490 puntos + 20) -> asciende a Plata.
                objects.add(new Object[]{490, Liga.BRONCE, 510, Liga.PLATA});

                // Caso 2: (600 puntos + 50) -> permanece en Plata. (Requiere Liga Inicial = PLATA)
        objects.add(new Object[]{600, Liga.PLATA, 650, Liga.PLATA});

                // Caso 3: (450 puntos + 20) -> permanece en Bronce.
        objects.add(new Object[]{450, Liga.BRONCE, 470, Liga.BRONCE});

                // Caso extra: Borde (499 + 1)
        objects.add(new Object[]{499, Liga.BRONCE, 500, Liga.PLATA});

                // Caso extra: Salto a Oro (para comprobar la lógica del IF/ELSE IF)
        objects.add(new Object[]{1490, Liga.PLATA, 1500, Liga.ORO});
        return objects;
    }

    // 4. Método @Test: Se ejecuta una vez por cada conjunto de parámetros
    @Test
    public void given_miembroEnBronce_when_sumaPuntos_then_subeLigaAPlata() {
        // Usamos los parámetros para inicializar el MiembroHogar
        MiembroHogar miembro = new MiembroHogar("Mario",18);
        miembro.setLiga(ligaInicial);
        // Act (Ejecución)
        servicio.actualizarPuntos(miembro, puntosAñadidos);

        // Assert (Verificación)
        assertEquals(ligaEsperada, miembro.getLiga(),"deberia ser igual");
    }
}