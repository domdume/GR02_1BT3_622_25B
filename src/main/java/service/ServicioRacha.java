package service;

import dao.MiembroHogarDAO;
import dao.QuehacerDAO;
import model.CalculadoraRacha;
import model.Dificultad;
import model.MiembroHogar;
import model.Quehacer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de dominio responsable de calcular rachas actuales por miembro.
 */
public class ServicioRacha {
    private final MiembroHogarDAO miembroDAO;
    private final QuehacerDAO quehacerDAO;
    private final CalculadoraRacha calculadoraRacha;

    public ServicioRacha() {
        this(new MiembroHogarDAO(), new QuehacerDAO(), new CalculadoraRacha());
    }

    // Constructor para pruebas (inyección de dependencias)
    public ServicioRacha(MiembroHogarDAO miembroDAO, QuehacerDAO quehacerDAO, CalculadoraRacha calculadoraRacha) {
        this.miembroDAO = miembroDAO;
        this.quehacerDAO = quehacerDAO;
        this.calculadoraRacha = calculadoraRacha != null ? calculadoraRacha : new CalculadoraRacha();
    }

    public RachaData obtenerRachasActuales() {
        List<MiembroHogar> miembros = miembroDAO.findAll();
        List<Quehacer> todasLasTareas = quehacerDAO.findAllWithMiembroHogar();

        Map<Long, List<LocalDate>> fechasPorMiembro = new HashMap<>();
        for (Quehacer q : todasLasTareas) {
            if (q.getMiembroHogar() != null && q.isCompletado() && q.getFechaFinalizacion() != null) {
                Long mid = q.getMiembroHogar().getId();
                if (mid == null) continue; // Igualar comportamiento seguro si no hay ID
                fechasPorMiembro.computeIfAbsent(mid, k -> new ArrayList<>())
                        .add(q.getFechaFinalizacion().toLocalDate());
            }
        }

        Map<Long, Integer> rachaPorMiembro = new HashMap<>();
        CalculadoraRacha calc = new CalculadoraRacha();
        for (MiembroHogar m : miembros) {
            int racha = 0;
            if (m.getId() != null) {
                List<LocalDate> fechas = fechasPorMiembro.getOrDefault(m.getId(), Collections.emptyList());
                racha = calc.calcularRacha(fechas, m.getRachaCongelada());
            }
            rachaPorMiembro.put(m.getId(), racha);
        }

        // Orden: racha desc, nombre asc para empates; nulls al final
        List<MiembroHogar> miembrosOrdenados = miembros.stream()
                .sorted((m1, m2) -> {
                    int r1 = rachaPorMiembro.getOrDefault(m1.getId(), 0);
                    int r2 = rachaPorMiembro.getOrDefault(m2.getId(), 0);
                    if (r1 != r2) return Integer.compare(r2, r1); // desc
                    String n1 = Optional.ofNullable(m1.getNombre()).orElse("");
                    String n2 = Optional.ofNullable(m2.getNombre()).orElse("");
                    return n1.compareToIgnoreCase(n2);
                })
                .collect(Collectors.toList());

        return new RachaData(miembrosOrdenados, rachaPorMiembro);
    }

    /**
     * Registra una tarea rápida para el miembro y la marca como completada en la fecha indicada.
     * Devuelve el nombre del miembro para mensajes.
     */
    public Optional<String> registrarTareaRapida(Long miembroId, LocalDate dia) {
        if (miembroId == null || dia == null) return Optional.empty();
        MiembroHogar miembro = miembroDAO.findById(miembroId);
        if (miembro == null) return Optional.empty();

        Quehacer q = new Quehacer("Tarea rápida de racha", LocalDateTime.now().plusHours(2), Dificultad.MEDIO);
        q.setMiembroHogar(miembro);
        quehacerDAO.create(q);

        q.marcarCompletado();
        q.setFechaFinalizacion(dia.atTime(12, 0));
        quehacerDAO.update(q);

        return Optional.ofNullable(miembro.getNombre());
    }

    /**
     * Versión pura para pruebas: calcula rachas y ordena usando miembros y tareas en memoria.
     */
    public RachaData calcularRachas(List<MiembroHogar> miembros, List<Quehacer> todasLasTareas) {
        if (miembros == null) miembros = Collections.emptyList();
        if (todasLasTareas == null) todasLasTareas = Collections.emptyList();

        Map<Long, List<LocalDate>> fechasPorMiembro = new HashMap<>();
        for (Quehacer q : todasLasTareas) {
            if (q == null || q.getMiembroHogar() == null || q.getFechaFinalizacion() == null) continue;
            if (!q.isCompletado()) continue;
            Long mid = q.getMiembroHogar().getId();
            if (mid == null) continue;
            fechasPorMiembro.computeIfAbsent(mid, k -> new ArrayList<>())
                    .add(q.getFechaFinalizacion().toLocalDate());
        }

        Map<Long, Integer> rachaPorMiembro = new HashMap<>();
        for (MiembroHogar m : miembros) {
            Integer racha = 0;
            if (m != null && m.getId() != null) {
                List<LocalDate> fechas = fechasPorMiembro.getOrDefault(m.getId(), Collections.emptyList());
                racha = calculadoraRacha.calcularRacha(fechas);
            }
            rachaPorMiembro.put(m != null ? m.getId() : null, racha);
        }

        List<MiembroHogar> miembrosOrdenados = miembros.stream()
                .filter(Objects::nonNull)
                .sorted((m1, m2) -> {
                    int r1 = rachaPorMiembro.getOrDefault(m1.getId(), 0);
                    int r2 = rachaPorMiembro.getOrDefault(m2.getId(), 0);
                    if (r1 != r2) return Integer.compare(r2, r1);
                    String n1 = Optional.ofNullable(m1.getNombre()).orElse("");
                    String n2 = Optional.ofNullable(m2.getNombre()).orElse("");
                    return n1.compareToIgnoreCase(n2);
                })
                .collect(Collectors.toList());

        return new RachaData(miembrosOrdenados, rachaPorMiembro);
    }

    protected int calcularRachaFechas(List<LocalDate> fechas, boolean isFrozen) {
        return new CalculadoraRacha().calcularRacha(fechas, isFrozen);
    }

    public static class RachaData {
        public final List<MiembroHogar> miembrosOrdenados;
        public final Map<Long, Integer> rachaPorMiembro;

        public RachaData(List<MiembroHogar> miembrosOrdenados, Map<Long, Integer> rachaPorMiembro) {
            this.miembrosOrdenados = miembrosOrdenados;
            this.rachaPorMiembro = rachaPorMiembro;
        }
    }
}