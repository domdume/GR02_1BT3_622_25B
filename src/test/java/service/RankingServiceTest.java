package service;

import model.MiembroHogar;
import model.Quehacer;
import model.EstadoQuehacer;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class RankingServiceTest {

    private MiembroHogar miembro(long id, String nombre) {
        MiembroHogar m = new MiembroHogar(nombre, 20);
        try {
            var f = MiembroHogar.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(m, id);
        } catch (Exception ignored) {
        }
        return m;
    }

    private Quehacer tareaCompletadaPara(MiembroHogar m, LocalDateTime fin) {
        Quehacer q = new Quehacer("t", fin.plusHours(1), model.Dificultad.MEDIO);
        q.setMiembroHogar(m);
        q.setFechaFinalizacion(fin);
        q.setEstado(EstadoQuehacer.COMPLETADO);
        return q;
    }

    @Test
    public void dado_ListaDeMiembrosVacia_Cuando_GeneraRankingRachas_Entonces_DevuelveListaVacia() {
        RankingService rs = new RankingService();
        var ranking = rs.getStreakRanking(Collections.emptyList(), Collections.emptyList());
        assertEquals(0, ranking.size());
    }
    
    @Test
    public void dado_DosMiembrosConRachasDistintas_Cuando_GeneraRanking_Entonces_DevuelveListaOrdenadaDescendentemente() {
        MiembroHogar ana = miembro(1L, "Ana");
        MiembroHogar beto = miembro(2L, "Beto");
        List<MiembroHogar> miembros = Arrays.asList(ana, beto);

        LocalDateTime base = LocalDateTime.now();
        List<Quehacer> tareas = new ArrayList<>();
        // Ana: 3 consecutivos
        for (int i=0;i<3;i++) tareas.add(tareaCompletadaPara(ana, base.minusDays(i)));
        // Beto: solo hoy
        tareas.add(tareaCompletadaPara(beto, base));

        RankingService rs = new RankingService();
        var ranking = rs.getStreakRanking(miembros, tareas);

        assertEquals(Arrays.asList(ana, beto), ranking);
    }
}