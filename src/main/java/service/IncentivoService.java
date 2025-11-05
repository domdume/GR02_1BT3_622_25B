package service;

import dao.IncentivoDAO;
import dao.MiembroHogarDAO;
import model.Incentivo;
import model.MiembroHogar;
import model.Quehacer;
import model.TipoIncentivo;
import java.util.function.Supplier;

public class IncentivoService {
    private final IncentivoDAO incentivoDAO;
    private final LigaService ligaService;
    private final LogroService logroService;

    // Factory para permitir tests que sustituyan la implementación real por un stub
    public static Supplier<IncentivoService> factory = () -> new IncentivoService();

    public static void setFactory(Supplier<IncentivoService> f) {
        if (f == null) throw new IllegalArgumentException("factory no puede ser nulo");
        factory = f;
    }

    public IncentivoService() {
        this.incentivoDAO = new IncentivoDAO();
        // Inyectar un AchievementRepository JPA por defecto para persistir logros
        this.ligaService = new LigaService();
        this.logroService = new LogroService();
    }

    // Constructor para testing con mock
    IncentivoService(IncentivoDAO incentivoDAO) {
        this.incentivoDAO = incentivoDAO;
        this.ligaService = new LigaService();
        this.logroService = new LogroService();
    }

    public IncentivoService(LogroService logroService) {
        this.incentivoDAO = new IncentivoDAO();
        this.ligaService = new LigaService();
        this.logroService = logroService;
    }

    public void aplicarIncentivo(MiembroHogar miembro, Quehacer quehacer) {
        if (miembro == null || quehacer == null) {
            throw new IllegalArgumentException("Miembro y quehacer no pueden ser nulos");
        }

        Incentivo incentivo = new Incentivo();
        
        if (quehacer.fueCompletadoATiempo()) {
            crearRecompensa(incentivo, miembro, quehacer);
        } else {
            crearPenalizacion(incentivo, miembro, quehacer);
        }

    // Establecer la relación bidireccional
        incentivo.setMiembroHogar(miembro);
        incentivo.setQuehacer(quehacer);
        miembro.anadirIncentivo(incentivo);

        // Persistir el incentivo
    incentivoDAO.create(incentivo);

    // Después de persistir, actualizar la liga (ya lo hacemos dentro de crearRecompensa/crearPenalizacion)
        // Persistir el miembro para que los cambios de puntos y liga se reflejen en la BD
        try {
            new MiembroHogarDAO().update(miembro);
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudo persistir MiembroHogar tras aplicar incentivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Versión que aplica el incentivo/penalización recibiendo los ids para evitar tocar colecciones
     * y problemas de lazy initialization cuando se llama desde contextos sin sesión.
     */
    public void aplicarIncentivoByIds(Long miembroId, Long quehacerId) {
        if (miembroId == null || quehacerId == null) throw new IllegalArgumentException("Ids no pueden ser nulos");

        // Cargar quehacer para conocer dificultad y estado
        model.Quehacer q = new dao.QuehacerDAO().findById(quehacerId);

        Incentivo incentivo = new Incentivo();
        boolean recompensa = q != null && q.fueCompletadoATiempo();

        int puntos = switch (q.getDificultad()) {
            case FACIL -> Incentivo.PUNTOS_FACIL;
            case MEDIO -> Incentivo.PUNTOS_MEDIO;
            case DIFICIL -> Incentivo.PUNTOS_DIFICIL;
        };

        if (recompensa) {
            incentivo.setTipoIncentivo(TipoIncentivo.RECOMPENSA);
            incentivo.setPuntos(puntos);
            incentivo.setDescripcion("Completado a tiempo: " + (q != null ? q.getNombre() : quehacerId));
        } else {
            incentivo.setTipoIncentivo(TipoIncentivo.PENALIZACION);
            incentivo.setPuntos(-puntos);
            incentivo.setDescripcion("No completado a tiempo: " + (q != null ? q.getNombre() : quehacerId));
        }

        // Persistir incentivo usando referencias para evitar lazy init
        incentivoDAO.createWithReferences(miembroId, quehacerId, incentivo);

        // Actualizar puntos del miembro usando DAO + LigaService
        MiembroHogarDAO miembroDao = new MiembroHogarDAO();
        model.MiembroHogar miembro = miembroDao.findById(miembroId);
        if (miembro != null) {
            // Capturar liga antes de modificar puntos
            model.Liga ligaAntes = miembro.getLiga();
            if (recompensa) {
                ligaService.actualizarPuntos(miembro, puntos);
            } else {
                ligaService.removerPuntos(miembro, puntos);
            }
            // Persistir cambios en miembro
            miembroDao.update(miembro);
            // Intentar asignar emblema si hubo ascenso
            try {
                logroService.asignarEmblemaAscenso(miembro, ligaAntes, miembro.getLiga());
            } catch (Exception ex) {
                System.out.println("[IncentivoService] Error al asignar emblema (byIds): " + ex.getMessage());
            }
            System.out.println("[IncentivoService] aplicarIncentivoByIds - miembroId=" + miembroId + " puntos actualizados a " + miembro.getPuntos());
        } else {
            System.out.println("[IncentivoService] aplicarIncentivoByIds - no se encontró miembroId=" + miembroId);
        }
    }

    private void crearRecompensa(Incentivo incentivo, MiembroHogar miembro, Quehacer quehacer) {
        incentivo.setTipoIncentivo(TipoIncentivo.RECOMPENSA);
        int puntos = switch (quehacer.getDificultad()) {
            case FACIL -> Incentivo.PUNTOS_FACIL;
            case MEDIO -> Incentivo.PUNTOS_MEDIO;
            case DIFICIL -> Incentivo.PUNTOS_DIFICIL;
        };
        incentivo.setPuntos(puntos);
        incentivo.setDescripcion("Completado a tiempo: " + quehacer.getNombre());
        int antes = miembro.getPuntos();
        // Capturar liga antes del cambio
        model.Liga ligaAntes = miembro.getLiga();
        // Usar el servicio de ligas para aplicar puntos y recalcular liga
        ligaService.actualizarPuntos(miembro, puntos);
        int despues = miembro.getPuntos();
        // Después de actualizar puntos/ligas, asignar emblema si hubo ascenso
        try {
            logroService.asignarEmblemaAscenso(miembro, ligaAntes, miembro.getLiga());
        } catch (Exception ex) {
            System.out.println("[IncentivoService] Error al asignar emblema: " + ex.getMessage());
        }
        System.out.println("👍 ¡Felicidades! " + miembro.getNombre() + " terminó '" + quehacer.getNombre() + "' a tiempo. Puntos añadidos: " + puntos + " (" + antes + " -> " + despues + ")");
    }

    private void crearPenalizacion(Incentivo incentivo, MiembroHogar miembro, Quehacer quehacer) {
        incentivo.setTipoIncentivo(TipoIncentivo.PENALIZACION);
        // Penalización equivalente a los puntos que otorgaría la dificultad
        int puntos = switch (quehacer.getDificultad()) {
            case FACIL -> Incentivo.PUNTOS_FACIL;
            case MEDIO -> Incentivo.PUNTOS_MEDIO;
            case DIFICIL -> Incentivo.PUNTOS_DIFICIL;
        };
        incentivo.setPuntos(-puntos);
        incentivo.setDescripcion("No completado a tiempo: " + quehacer.getNombre());
        int antes = miembro.getPuntos();
        // Usar el servicio de ligas para remover puntos (evita negativos)
        ligaService.removerPuntos(miembro, puntos);
        int despues = miembro.getPuntos();
        System.out.println("👎 Lástima, " + miembro.getNombre() + " se retrasó con '" + quehacer.getNombre() + "'. Penalización: -" + puntos + " puntos. (" + antes + " -> " + despues + ")");
    }
}
