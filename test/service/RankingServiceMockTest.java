package service;

import model.MiembroHogar;
import model.Quehacer;
import model.EstadoQuehacer;
import org.junit.Test;
import repository.TaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RankingServiceMockTest {

    private MiembroHogar miembro(long id, String nombre) {
        MiembroHogar m = new MiembroHogar(nombre, 20);
        m.setId(id);
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
    public void dado_ListaDeTresMiembros_Cuando_GeneraRanking_Entonces_PideElHistorialDeTareasParaCadaMiembro() {
        TaskRepository mockRepo = mock(TaskRepository.class);
        // Subclase que usa repo pero cálculo real
        RankingService service = new RankingService(mockRepo);

        MiembroHogar a = miembro(1L, "Ana");
        MiembroHogar b = miembro(2L, "Beto");
        MiembroHogar c = miembro(3L, "Carlos");
        List<MiembroHogar> miembros = Arrays.asList(a, b, c);

        LocalDateTime base = LocalDateTime.now();
        when(mockRepo.getCompletedTasksByUser(1L)).thenReturn(Collections.singletonList(tareaCompletadaPara(a, base)));
        when(mockRepo.getCompletedTasksByUser(2L)).thenReturn(Collections.singletonList(tareaCompletadaPara(b, base.minusDays(1))));
        when(mockRepo.getCompletedTasksByUser(3L)).thenReturn(Collections.singletonList(tareaCompletadaPara(c, base.minusDays(2))));

        List<MiembroHogar> ranking = service.getStreakRanking(miembros);

        verify(mockRepo, times(1)).getCompletedTasksByUser(1L);
        verify(mockRepo, times(1)).getCompletedTasksByUser(2L);
        verify(mockRepo, times(1)).getCompletedTasksByUser(3L);
        verify(mockRepo, times(3)).getCompletedTasksByUser(anyLong());
        assertEquals(3, ranking.size());
    }

    @Test
    public void dado_ListaDeTresMiembros_Cuando_GeneraRanking_Entonces_SeCalculaRachaTresVeces() {
        TaskRepository mockRepo = mock(TaskRepository.class);
        class CountingRankingService extends RankingService {
            int calls = 0;
            CountingRankingService(TaskRepository repo) { super(repo); }
            @Override
            protected int calcularRachaFechas(List<LocalDate> fechas) { calls++; return 1; }
        }
        CountingRankingService service = new CountingRankingService(mockRepo);
        MiembroHogar a = miembro(1L, "Ana");
        MiembroHogar b = miembro(2L, "Beto");
        MiembroHogar c = miembro(3L, "Carlos");
        List<MiembroHogar> miembros = Arrays.asList(a, b, c);
        LocalDateTime base = LocalDateTime.now();
        when(mockRepo.getCompletedTasksByUser(1L)).thenReturn(Collections.singletonList(tareaCompletadaPara(a, base)));
        when(mockRepo.getCompletedTasksByUser(2L)).thenReturn(Collections.singletonList(tareaCompletadaPara(b, base.minusDays(1))));
        when(mockRepo.getCompletedTasksByUser(3L)).thenReturn(Collections.singletonList(tareaCompletadaPara(c, base.minusDays(2))));
        service.getStreakRanking(miembros);
        assertEquals(3, service.calls);
    }
}
