package service;

import model.MiembroHogar;
import model.Quehacer;
import model.EstadoQuehacer;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class ServicioRachaTest {
    private ServicioRacha servicio;

    @Before
    public void setup() {
        servicio = new ServicioRacha();
    }

    private MiembroHogar miembro(long id, String nombre) {
        MiembroHogar m = new MiembroHogar(nombre, 20);
        try {
            var f = MiembroHogar.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(m, id);
        } catch (Exception ignored) {}
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
    public void escenario_11_OrdenDescendente_porRacha() {
        MiembroHogar A = miembro(1L, "A");
        MiembroHogar B = miembro(2L, "B");
        MiembroHogar C = miembro(3L, "C");
        List<MiembroHogar> miembros = Arrays.asList(A, B, C);

        LocalDateTime base = LocalDateTime.now();
        List<Quehacer> tareas = new ArrayList<>();
        for (int i=0;i<7;i++) tareas.add(tareaCompletadaPara(C, base.minusDays(i)));
        for (int i=0;i<5;i++) tareas.add(tareaCompletadaPara(A, base.minusDays(i)));
        for (int i=0;i<2;i++) tareas.add(tareaCompletadaPara(B, base.minusDays(i)));

        var data = servicio.calcularRachas(miembros, tareas);

        assertEquals(Arrays.asList(C, A, B), data.miembrosOrdenados);
        assertEquals(7, data.rachaPorMiembro.get(C.getId()).intValue());
        assertEquals(5, data.rachaPorMiembro.get(A.getId()).intValue());
        assertEquals(2, data.rachaPorMiembro.get(B.getId()).intValue());
    }

    @Test
    public void escenario_12_Empates_alfabetico() {
        MiembroHogar Ana = miembro(1L, "Ana");
        MiembroHogar Beto = miembro(2L, "Beto");
        MiembroHogar Carlos = miembro(3L, "Carlos");
        List<MiembroHogar> miembros = Arrays.asList(Ana, Beto, Carlos);

        LocalDateTime base = LocalDateTime.now();
        List<Quehacer> tareas = new ArrayList<>();
        for (int i=0;i<5;i++) tareas.add(tareaCompletadaPara(Ana, base.minusDays(i)));
        for (int i=0;i<2;i++) tareas.add(tareaCompletadaPara(Beto, base.minusDays(i)));
        for (int i=0;i<2;i++) tareas.add(tareaCompletadaPara(Carlos, base.minusDays(i)));

        var data = servicio.calcularRachas(miembros, tareas);

        assertEquals(Arrays.asList(Ana, Beto, Carlos), data.miembrosOrdenados);
        assertEquals(5, data.rachaPorMiembro.get(Ana.getId()).intValue());
        assertEquals(2, data.rachaPorMiembro.get(Beto.getId()).intValue());
        assertEquals(2, data.rachaPorMiembro.get(Carlos.getId()).intValue());
    }

    @Test
    public void escenario_13_RachaCero_alFinal() {
        MiembroHogar Ana = miembro(1L, "Ana");
        MiembroHogar Beto = miembro(2L, "Beto");
        List<MiembroHogar> miembros = Arrays.asList(Ana, Beto);

        LocalDateTime base = LocalDateTime.now();
        List<Quehacer> tareas = new ArrayList<>();
        for (int i=0;i<3;i++) tareas.add(tareaCompletadaPara(Ana, base.minusDays(i)));

        var data = servicio.calcularRachas(miembros, tareas);

        assertEquals(Arrays.asList(Ana, Beto), data.miembrosOrdenados);
        assertEquals(3, data.rachaPorMiembro.get(Ana.getId()).intValue());
        assertEquals(0, data.rachaPorMiembro.get(Beto.getId()).intValue());
    }
}
