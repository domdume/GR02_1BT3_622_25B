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
}