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
        this.miembroDAO = new MiembroHogarDAO();
        this.quehacerDAO = new QuehacerDAO();
        this.calculadoraRacha = new CalculadoraRacha();
    }

    public RachaData obtenerRachasActuales() {
        List<MiembroHogar> miembros = miembroDAO.findAll();
        List<Quehacer> todasLasTareas = quehacerDAO.findAllWithMiembroHogar();

        Map<Long, List<LocalDate>> fechasPorMiembro = new HashMap<>();
        for (Quehacer q : todasLasTareas) {
            if (q.getMiembroHogar() != null && q.estaCompletado() && q.getFechaFinalizacion() != null) {
                Long mid = q.getMiembroHogar().getId();
                fechasPorMiembro.computeIfAbsent(mid, k -> new ArrayList<>())
                        .add(q.getFechaFinalizacion().toLocalDate());
            }
        }

        Map<Long, Integer> rachaPorMiembro = new HashMap<>();
        for (MiembroHogar m : miembros) {
            List<LocalDate> fechas = fechasPorMiembro.getOrDefault(m.getId(), Collections.emptyList());
            int racha = calculadoraRacha.calcularRacha(fechas);
            rachaPorMiembro.put(m.getId(), racha);
        }

        List<MiembroHogar> miembrosOrdenados = miembros.stream()
                .sorted(Comparator.comparingInt(m -> -rachaPorMiembro.getOrDefault(m.getId(), 0)))
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

    public static class RachaData {
        public final List<MiembroHogar> miembrosOrdenados;
        public final Map<Long, Integer> rachaPorMiembro;

        public RachaData(List<MiembroHogar> miembrosOrdenados, Map<Long, Integer> rachaPorMiembro) {
            this.miembrosOrdenados = miembrosOrdenados;
            this.rachaPorMiembro = rachaPorMiembro;
        }
    }
}
