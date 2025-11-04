package service;

import dao.MiembroHogarDAO;
import dao.QuehacerDAO;
import model.CalculadoraRacha;
import model.Dificultad;
import model.MiembroHogar;
import model.Quehacer;
import model.EstadoQuehacer;
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

    private Map<Long, List<LocalDate>> procesarFechasPorMiembro(List<Quehacer> todasLasTareas) {
        Map<Long, List<LocalDate>> fechasPorMiembro = new HashMap<>();
        for (Quehacer q : todasLasTareas) {
            if (q.getMiembroHogar() != null && q.isCompletado() && q.getFechaFinalizacion() != null) {
                Long mid = q.getMiembroHogar().getId();
                if (mid == null) continue;
                fechasPorMiembro.computeIfAbsent(mid, k -> new ArrayList<>())
                        .add(q.getFechaFinalizacion().toLocalDate());
            }
        }
        return fechasPorMiembro;
    }

    public RachaData obtenerRachasActuales() {
        List<MiembroHogar> miembros = miembroDAO.findAll();
        List<Quehacer> todasLasTareas = quehacerDAO.findAllWithMiembroHogar();

        Map<Long, List<LocalDate>> fechasPorMiembro = procesarFechasPorMiembro(todasLasTareas);

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


    public Optional<String> registrarTareaRapida(Long miembroId, LocalDate dia) {
        if (miembroId == null || dia == null) return Optional.empty();

        // 1. Obtener el miembro.
        MiembroHogar miembro = miembroDAO.findById(miembroId);
        if (miembro == null) return Optional.empty();

        // 2. Definir tiempos límite para la tarea
        final LocalDateTime FINAL_DEL_DIA = dia.atTime(23, 59, 59);
        final LocalDateTime MOMENTO_COMPLETADO = dia.atTime(23, 58, 0);

        // 3. Crear el Quehacer con un límite futuro (para ese día)
        Quehacer tareaRapida = new Quehacer("Tarea rápida de racha", FINAL_DEL_DIA, Dificultad.MEDIO);
        tareaRapida.setMiembroHogar(miembro);

        // 4. Persistir el Quehacer ANTES de completarlo.
        quehacerDAO.create(tareaRapida);

        // 5. Marcar como COMPLETADO con una fecha de finalización dentro del día especificado
        tareaRapida.setFechaFinalizacion(MOMENTO_COMPLETADO);
        tareaRapida.setEstado(EstadoQuehacer.COMPLETADO);
        quehacerDAO.update(tareaRapida);

        // 6. Retornar el nombre para mensajes
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