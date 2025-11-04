package service;

import model.Dificultad;
import model.EstadoQuehacer;
import model.MiembroHogar;
import model.Quehacer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class RankingServiceParametersTest {

    private RankingService rankingService;

    private final String nombre1;
    private final int racha1;
    private final String nombre2;
    private final int racha2;
    private final List<String> expectedOrder;

    public RankingServiceParametersTest(String nombre1, int racha1, String nombre2, int racha2, List<String> expectedOrder) {
        this.nombre1 = nombre1;
        this.racha1 = racha1;
        this.nombre2 = nombre2;
        this.racha2 = racha2;
        this.expectedOrder = expectedOrder;
    }

    @Before
    public void setUp() {
        rankingService = new RankingService();
    }

    @Parameters
    public static Collection<Object[]> parameters() {
        List<Object[]> cases = new ArrayList<>();
        // (Ana: 5, Beto: 2) -> [Ana, Beto]
        cases.add(new Object[]{"Ana", 5, "Beto", 2, Arrays.asList("Ana", "Beto")});
        // (Ana: 2, Beto: 5) -> [Beto, Ana]
        cases.add(new Object[]{"Ana", 2, "Beto", 5, Arrays.asList("Beto", "Ana")});
        // (Ana: 5, Beto: 5) -> [Ana, Beto] (alfabético)
        cases.add(new Object[]{"Ana", 5, "Beto", 5, Arrays.asList("Ana", "Beto")});
        // (Carlos: 5, Ana: 5) -> [Ana, Carlos] (alfabético)
        cases.add(new Object[]{"Carlos", 5, "Ana", 5, Arrays.asList("Ana", "Carlos")});
        return cases;
    }

    @Test
    public void dado_VariosMiembrosConEmpates_Cuando_GeneraRanking_Entonces_OrdenaPorRachaLuegoPorNombre() {
        MiembroHogar m1 = new MiembroHogar(nombre1, 20); m1.setId(1L);
        MiembroHogar m2 = new MiembroHogar(nombre2, 21); m2.setId(2L);
        LocalDateTime base = LocalDateTime.now();
        List<Quehacer> tareas = new ArrayList<>();
        tareas.addAll(crearRacha(m1, racha1, base));
        tareas.addAll(crearRacha(m2, racha2, base));
        List<MiembroHogar> miembros = Arrays.asList(m1, m2);
        var ranking = rankingService.getStreakRanking(miembros, tareas);
        List<String> nombresOrdenados = ranking.stream().map(MiembroHogar::getNombre).collect(Collectors.toList());
        assertEquals(expectedOrder, nombresOrdenados);
    }

    private List<Quehacer> crearRacha(MiembroHogar m, int racha, LocalDateTime base) {
        List<Quehacer> list = new ArrayList<>();
        for (int i = 0; i < racha; i++) {
            Quehacer q = new Quehacer("t", base.plusHours(1), Dificultad.MEDIO);
            q.setMiembroHogar(m);
            q.setFechaFinalizacion(base.minusDays(i));
            q.setEstado(EstadoQuehacer.COMPLETADO);
            list.add(q);
        }
        return list;
    }
}