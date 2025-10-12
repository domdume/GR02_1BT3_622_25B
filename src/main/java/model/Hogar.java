package model;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Singleton que representa el hogar y centraliza el registro de miembros y quehaceres.
 * Implementa la lógica de asignación automática de tareas.
 */
public class Hogar {
    private static final Hogar instance = new Hogar();
    private List<MiembroHogar> miembros;
    private List<Quehacer> quehaceres;

    private Hogar() {
        this.miembros = new ArrayList<>();
        this.quehaceres = new ArrayList<>();
    }

    public static Hogar getInstance() {
        return instance;
    }

    /**
     * Registra un nuevo miembro en el hogar
     */
    public void registrarMiembro(MiembroHogar miembro) {
        if (miembro != null && !miembros.contains(miembro)) {
            this.miembros.add(miembro);
            System.out.println("[Hogar] Miembro registrado: " + miembro.getNombre());
        }
    }

    /**
     * Asigna automáticamente un quehacer al miembro con menor carga de trabajo
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
}
