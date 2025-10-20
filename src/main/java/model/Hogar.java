package model;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Clase Singleton que representa el hogar y centraliza el registro de miembros y quehaceres.
 * Implementa la lógica de asignación automática de tareas.
 */
public class Hogar {
    private static final Hogar instance = new Hogar();
    private List<MiembroHogar> miembros;
    private List<Quehacer> quehaceres;
    private RegistroQuehacer registroQuehacer;

    private Hogar() {
        this.miembros = new ArrayList<>();
        this.quehaceres = new ArrayList<>();
        this.registroQuehacer = new RegistroQuehacer();
    }

    public static Hogar getInstance() {
        return instance;
    }

    /**
     * Registra un nuevo miembro en el hogar y lo suscribe automáticamente
     * al sistema de notificaciones Observer.
     */
    public void registrarMiembro(MiembroHogar miembro) {
        if (miembro != null && !miembros.contains(miembro)) {
            this.miembros.add(miembro);
            registroQuehacer.suscribirMiembroAutomaticamente(miembro);
            System.out.println("[Hogar] Miembro registrado: " + miembro.getNombre());
        }
    }

    /**
     * Asigna automáticamente un quehacer al miembro con menor carga de trabajo
     * y notifica a todos los observadores suscritos sobre la nueva tarea.
     */
    public void registrarQuehacer(Quehacer quehacer) {
        if (miembros.isEmpty()) {
            System.out.println("[Hogar] No hay miembros disponibles para asignar tarea");
            return;
        }

        // Algoritmo de asignación automática: miembro con menos quehaceres
        MiembroHogar miembroAsignado = miembros.get(0);
        for (MiembroHogar miembro : miembros) {
            if (miembro.getQuehaceres().size() < miembroAsignado.getQuehaceres().size()) {
                miembroAsignado = miembro;
            }
        }

        miembroAsignado.asignarQuehacer(quehacer);
        this.quehaceres.add(quehacer);
        //Integración con patrón Observer
        registroQuehacer.agregarQuehacer(quehacer);
        System.out.println("[Hogar] Tarea '" + quehacer.getNombre() + "' asignada a " + miembroAsignado.getNombre());
    }

    /**
     * Obtiene el jefe del hogar si existe
     */
    public JefeDelHogar getJefeDelHogar() {
        return miembros.stream()
                .filter(m -> m instanceof JefeDelHogar)
                .map(m -> (JefeDelHogar) m)
                .findFirst()
                .orElse(null);
    }

    // Getters
    public List<MiembroHogar> getRegistroMiembro() {
        return miembros;
    }

    public List<Quehacer> getRegistroQuehacer() {
        return quehaceres;
    }

    public RegistroQuehacer getObservadorQuehacer() {
        return registroQuehacer;
    }

    /**
     * Busca un miembro por nombre
     */
    public MiembroHogar buscarMiembroPorNombre(String nombre) {
        return miembros.stream()
                .filter(miembro -> miembro.getNombre().equalsIgnoreCase(nombre.trim()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Calcula estadísticas del hogar
     */
    public int getTotalMiembros() {
        return miembros.size();
    }

    public int getTotalQuehaceres() {
        return quehaceres.size();
    }

    public boolean tieneJefe() {
        return getJefeDelHogar() != null;
    }

    /**
     * Inicializa el sistema Observer suscribiendo todos los miembros existentes
     * al RegistroQuehacer para recibir notificaciones de nuevas tareas.
     */
    private void inicializarSistemaObserver() {
        for (MiembroHogar miembro : miembros) {
            registroQuehacer.suscribir(miembro);
            System.out.println("[Observer] Miembro " + miembro.getNombre() + " suscrito a notificaciones");
        }
        System.out.println("[Observer] Sistema de notificaciones inicializado con " + miembros.size() + " observadores");
    }

}