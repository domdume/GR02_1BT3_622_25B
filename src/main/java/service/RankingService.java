package service;

import model.MiembroHogar;
import model.Quehacer;
import repository.TaskRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class RankingService {
    private final TaskRepository taskRepository;

    public RankingService() {
        this.taskRepository = null;
    }

    public RankingService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<MiembroHogar> getStreakRanking(List<MiembroHogar> miembros, List<Quehacer> tareas) {
        if (miembros == null || miembros.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<LocalDate>> fechasPorMiembro = groupTaskDatesByMember(tareas);
        Map<Long, Integer> rachas = calculateStreaks(miembros, fechasPorMiembro);

        return orderByRachaYNombre(miembros, rachas);
    }

    public List<MiembroHogar> getStreakRanking(List<MiembroHogar> miembros) {
        if (miembros == null || miembros.isEmpty()) {
            return Collections.emptyList();
        }
        if (taskRepository == null) {
            throw new IllegalStateException("TaskRepository no configurado");
        }

        Map<Long, Integer> rachas = calculateStreaksFromRepository(miembros);
        return orderByRachaYNombre(miembros, rachas);
    }

    private Map<Long, List<LocalDate>> groupTaskDatesByMember(List<Quehacer> tareas) {
        if (tareas == null) {
            return Collections.emptyMap();
        }

        return tareas.stream()
            .filter(q -> q != null && q.getMiembroHogar() != null && q.getFechaFinalizacion() != null)
            .collect(Collectors.groupingBy(
                q -> q.getMiembroHogar().getId(),
                Collectors.mapping(q -> q.getFechaFinalizacion().toLocalDate(), Collectors.toList())
            ));
    }

    private Map<Long, Integer> calculateStreaks(List<MiembroHogar> miembros, Map<Long, List<LocalDate>> fechasPorMiembro) {
        return miembros.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                MiembroHogar::getId,
                m -> calcularRachaFechasFrozen(fechasPorMiembro.getOrDefault(m.getId(), Collections.emptyList()), m.getRachaCongelada())
            ));
    }

    private Map<Long, Integer> calculateStreaksFromRepository(List<MiembroHogar> miembros) {
        return miembros.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                MiembroHogar::getId,
                m -> {
                    List<Quehacer> tareas = taskRepository.getCompletedTasksByUser(m.getId());
                    List<LocalDate> fechas = tareas == null ? Collections.emptyList() : tareas.stream()
                        .filter(q -> q != null && q.getFechaFinalizacion() != null)
                        .map(q -> q.getFechaFinalizacion().toLocalDate())
                        .collect(Collectors.toList());
                    return calcularRachaFechasFrozen(fechas, m.getRachaCongelada());
                }
            ));
    }

    protected int calcularRachaFechas(List<LocalDate> fechas) {
        return new model.CalculadoraRacha().calcularRacha(fechas);
    }

    protected int calcularRachaFechasFrozen(List<LocalDate> fechas, boolean frozen) {
        return new model.CalculadoraRacha().calcularRacha(fechas, frozen);
    }

    private Comparator<MiembroHogar> createComparator(Map<Long, Integer> rachas) {
        return Comparator.comparing((MiembroHogar m) -> {
            int racha = rachas.getOrDefault(m.getId(), 0);
            return racha;
        }).reversed()
        .thenComparing(m -> {
            String nombre = Optional.ofNullable(m.getNombre()).orElse("");
            return nombre;
        }, String.CASE_INSENSITIVE_ORDER);
    }

    private List<MiembroHogar> orderByRachaYNombre(List<MiembroHogar> miembros, Map<Long, Integer> rachas) {
        Comparator<MiembroHogar> comparator = createComparator(rachas);
        return miembros.stream()
            .filter(Objects::nonNull)
            .sorted(comparator)
            .collect(Collectors.toList());
    }
}