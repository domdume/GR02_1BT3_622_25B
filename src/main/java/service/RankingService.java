package service;

import model.MiembroHogar;
import model.Quehacer;
import repository.TaskRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class RankingService {
    private final TaskRepository taskRepository;

    public RankingService() { this.taskRepository = null; }
    public RankingService(TaskRepository taskRepository) { this.taskRepository = taskRepository; }

    public List<MiembroHogar> getStreakRanking(List<MiembroHogar> miembros, List<Quehacer> tareas) {
        if (miembros == null || miembros.isEmpty()) return Collections.emptyList();
        if (tareas == null) tareas = Collections.emptyList();

        Map<Long, List<LocalDate>> fechasPorMiembro = new HashMap<>();
        for (Quehacer q : tareas) {
            if (q == null || q.getMiembroHogar() == null || q.getFechaFinalizacion() == null) continue;
            Long id = q.getMiembroHogar().getId(); if (id == null) continue;
            fechasPorMiembro.computeIfAbsent(id, k -> new ArrayList<>()).add(q.getFechaFinalizacion().toLocalDate());
        }

        final Map<Long, Integer> rachas = new HashMap<>();
        for (MiembroHogar m : miembros) {
            int r = 0;
            if (m != null && m.getId() != null) {
                List<LocalDate> fechas = fechasPorMiembro.getOrDefault(m.getId(), Collections.emptyList());
                r = calcularRachaFechasFrozen(fechas, m.getRachaCongelada());
            }
            rachas.put(m != null ? m.getId() : null, r);
        }
        return orderByRachaYNombre(miembros, rachas);
    }

    public List<MiembroHogar> getStreakRanking(List<MiembroHogar> miembros) {
        if (miembros == null || miembros.isEmpty()) return Collections.emptyList();
        if (taskRepository == null) throw new IllegalStateException("TaskRepository no configurado");

        final Map<Long, Integer> rachas = new HashMap<>();
        for (MiembroHogar m : miembros) {
            int r = 0;
            if (m != null && m.getId() != null) {
                List<Quehacer> tareas = taskRepository.getCompletedTasksByUser(m.getId());
                List<LocalDate> fechas = new ArrayList<>();
                if (tareas != null) for (Quehacer q : tareas) if (q != null && q.getFechaFinalizacion() != null) fechas.add(q.getFechaFinalizacion().toLocalDate());
                r = calcularRachaFechasFrozen(fechas, m.getRachaCongelada());
            }
            rachas.put(m != null ? m.getId() : null, r);
        }
        return orderByRachaYNombre(miembros, rachas);
    }

    protected int calcularRachaFechas(List<LocalDate> fechas) {
        return new model.CalculadoraRacha().calcularRacha(fechas);
    }
    protected int calcularRachaFechasFrozen(List<LocalDate> fechas, boolean frozen) {
        return new model.CalculadoraRacha().calcularRacha(fechas, frozen);
    }

    private List<MiembroHogar> orderByRachaYNombre(List<MiembroHogar> miembros, Map<Long, Integer> rachas) {
        return miembros.stream().filter(Objects::nonNull).sorted((a,b)->{
            int ra = rachas.getOrDefault(a.getId(),0); int rb = rachas.getOrDefault(b.getId(),0);
            if (ra!=rb) return Integer.compare(rb,ra);
            String na = Optional.ofNullable(a.getNombre()).orElse("");
            String nb = Optional.ofNullable(b.getNombre()).orElse("");
            return na.compareToIgnoreCase(nb);
        }).collect(Collectors.toList());
    }
}
