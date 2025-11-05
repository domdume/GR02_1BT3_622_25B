package service;

import model.Logro;
import repository.AchievementRepository;
import repository.JpaAchievementRepository;
import model.MiembroHogar;
import model.Liga;
import model.TipoLogro;

import java.util.Optional;

/**
 * Servicio para gestionar la lógica de logros.
 * Se proporciona un constructor por defecto que crea un repository JPA
 * para que los métodos no lancen NullPointerException si se usa el ctor vacío.
 */
public class LogroService {
    public static final String LOGRO_RACHA_3 = "LOGRO_3";
    public static final String LOGRO_RACHA_7 = "LOGRO_7";
    private final AchievementRepository achievementRepository;

    public LogroService() {
        // Inyectar implementación por defecto basada en JPA
        this.achievementRepository = new JpaAchievementRepository();
    }

    public LogroService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public void asignarEmblemaAscenso(MiembroHogar miembro, Liga ligaAntes, Liga ligaDespues) {
        if (miembro == null || ligaAntes == null || ligaDespues == null) return;
        try {
            // Introducir variable explicativa
            boolean huboAscenso = ligaDespues.getNivel() > ligaAntes.getNivel();
            if (!huboAscenso) return;

            Long miembroId = miembro.getId();
            if (miembroId == null) return;

            // Si es la primera insignia del usuario, asignar Aprendiz Constante
            if (asignarEmblemaPrimeraVez(miembroId)) return;

            // Mapear ascenso -> emblema y persistir si aplica
            emblemaParaAscenso(ligaAntes, ligaDespues).ifPresent(id -> achievementRepository.guardarLogro(miembroId, id));
        } catch (Exception ex) {
            System.out.println("[ERROR] asignarEmblemaAscenso: " + ex.getMessage());
        }
    }

    // Extraer método: asigna el emblema por primera vez si corresponde.
    private boolean asignarEmblemaPrimeraVez(Long miembroId) {
        if (!achievementRepository.tieneCualquierLogro(miembroId)) {
            achievementRepository.guardarLogro(miembroId, "EMBLEMA_APRENDIZ_CONSTANTE");
            return true;
        }
        return false;
    }

    // Extraer método: devuelve el id del emblema correspondiente al ascenso si existe
    private java.util.Optional<String> emblemaParaAscenso(Liga antes, Liga despues) {
        if (antes == Liga.BRONCE && despues == Liga.PLATA) {
            return java.util.Optional.of("EMBLEMA_EXPLORADOR_PERSISTENTE");
        }
        if (antes == Liga.PLATA && despues == Liga.ORO) {
            return java.util.Optional.of("EMBLEMA_MAESTRO_QUEHACERES");
        }
        return java.util.Optional.empty();
    }
    /**
     * Verifica y asigna, si corresponde, el logro de racha alcanzado.
     * Regla de precedencia: si racha >= 7, se evalúa primero el logro de 7 días
     * y no se otorga el de 3 días en el mismo evento. Si no aplica 7, se evalúa 3.
     *
     * @param miembroId  id del miembro
     * @param rachaActual racha actual calculada externamente
     * @return mensaje opcional con la notificación a mostrar
     */
    public Optional<String> verificarYAsignarLogroRacha(Long miembroId, int rachaActual) {
        return procesarRacha(miembroId, rachaActual).map(AchievementNotification::getMensaje);
    }

    // Variante que también devuelve el tipo de logro ganado
    public Optional<AchievementNotification> verificarYAsignarLogroRachaConTipo(Long miembroId, int rachaActual) {
        return procesarRacha(miembroId, rachaActual);
    }

    // Extraer y centralizar la lógica de racha para evitar duplicación
    private Optional<AchievementNotification> procesarRacha(Long miembroId, int rachaActual) {
        if (miembroId == null) return Optional.empty();
        if (rachaActual >= 7) {
            if (!achievementRepository.tieneLogro(miembroId, LOGRO_RACHA_7)) {
                achievementRepository.guardarLogro(miembroId, LOGRO_RACHA_7);
                return Optional.of(new AchievementNotification(LOGRO_RACHA_7, TipoLogro.LOGRO_RACHA, mensajeLogro7()));
            }
            // Si ya tenía el de 7, no otorgar el de 3 en este evento
            return Optional.empty();
        }
        if (rachaActual >= 3) {
            if (!achievementRepository.tieneLogro(miembroId, LOGRO_RACHA_3)) {
                achievementRepository.guardarLogro(miembroId, LOGRO_RACHA_3);
                return Optional.of(new AchievementNotification(LOGRO_RACHA_3, TipoLogro.LOGRO_RACHA, mensajeLogro3()));
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

    public String mensajeLogro3() {
        return "¡Felicidades!, Ha ganado el logro “Racha de 3 días: Chispazo”";
    }
    public String mensajeLogro7() {
        return "¡Increíble! Ha ganado el logro “Racha de 7 días: Semana Perfecta”";
    }

    public void verificarLogroPorQuehaceres(MiembroHogar miembro) {
        if (miembro == null) {
            throw new IllegalArgumentException("El miembro no puede ser nulo");
        }

        // Nuevo comportamiento: medallas por umbrales que se duplican (2,4,8,16...)
        int tareas = miembro.getTareasCompletadas();
        if (tareas <= 0) {
            System.out.println("ℹ️ " + miembro.getNombre() + " aún no cumple los requisitos para una medalla.");
            return;
        }

        // Calcular umbrales alcanzados (2^k) hasta el número de tareas actual
        java.util.List<Integer> umbrales = new java.util.ArrayList<>();
        for (int um = 2; um <= tareas; um *= 2) {
            umbrales.add(um);
            // proteger contra overflow infinito
            if (um > Integer.MAX_VALUE / 2) break;
        }

        boolean otorgado = false;
        for (int umbral : umbrales) {
            // Si ya tiene una medalla para ese umbral, la saltamos (revisar persistencia primero)
            Long miembroId = miembro.getId();
            String logroId = "MEDALLA_" + umbral;
            boolean tienePersistente = (miembroId != null) && achievementRepository.tieneLogro(miembroId, logroId);
            boolean tieneEnMemoria = miembro.getLogros().stream()
                    .anyMatch(l -> l.getTipo() == TipoLogro.MEDALLA && l.getTareasRequeridas() == umbral);
            if (!tienePersistente && !tieneEnMemoria) {
                // Persistir usando el repository si es posible
                if (miembroId != null) {
                    try {
                        achievementRepository.guardarLogro(miembroId, logroId);
                    } catch (Exception ex) {
                        System.out.println("[WARN] No se pudo persistir medalla " + logroId + " para miembroId=" + miembroId + ": " + ex.getMessage());
                    }
                }
                // Añadir al objeto en memoria para que la vista actual lo muestre sin necesidad de recargar desde BD
                Logro logro = new Logro(logroId, TipoLogro.MEDALLA, umbral);
                miembro.addLogro(logro);
                System.out.println("🏅 Se otorgó medalla por " + umbral + " tareas a " + miembro.getNombre());
                otorgado = true;
            }
        }
        if (!otorgado) {
            System.out.println("ℹ️ " + miembro.getNombre() + " aún no cumple los requisitos para una nueva medalla.");
        }


    }

    public Logro verificarLogro(MiembroHogar miembro) {
        if (miembro == null) return null;

        int tareas = miembro.getTareasCompletadas();
        if (tareas < 2) return null;

        // Buscar el mayor umbral 2^k <= tareas para el que aún no tenga medalla
        int umbral = 2;
        int ultimo = 0;
        while (umbral <= tareas) {
            ultimo = umbral;
            if (umbral > Integer.MAX_VALUE / 2) break;
            umbral *= 2;
        }

    // Si ya tiene medalla para el último umbral, no otorgar
    final int ultimoFinal = ultimo;
    boolean tiene = miembro.getLogros().stream()
        .anyMatch(l -> l.getTipo() == TipoLogro.MEDALLA && l.getTareasRequeridas() == ultimoFinal);
    if (tiene) return null;

    Logro medalla = new Logro("MEDALLA_" + ultimoFinal, TipoLogro.MEDALLA, ultimoFinal);
        miembro.addLogro(medalla);
        return medalla;
    }

    // DTO simple para notificar tipo y mensaje del logro
    public static class AchievementNotification {
        private final String logroId;
        private final TipoLogro tipo;
        private final String mensaje;

        public AchievementNotification(String logroId, TipoLogro tipo, String mensaje) {
            this.logroId = logroId;
            this.tipo = tipo;
            this.mensaje = mensaje;
        }

        public String getLogroId() { return logroId; }
        public TipoLogro getTipo() { return tipo; }
        public String getMensaje() { return mensaje; }

}}
