package gui;

import model.Hogar;
import model.MiembroHogar;
import model.Quehacer;

/**
 * Clase Boundary para la interfaz del Miembro de la Familia
 * Según diagrama UML - Package GUI
 */
public class InterfazDelMiembroFamilia {
    
    private MiembroHogar miembro;
    private Hogar hogar;
    
    public InterfazDelMiembroFamilia(MiembroHogar miembro) {
        this.miembro = miembro;
        this.hogar = Hogar.getInstance();
    }
    
    /**
     * Método para completar un quehacer
     * Según diagrama UML
     */
    public void completarUnQuehacer() {
        System.out.println("=== INTERFAZ DEL MIEMBRO: Completar Quehacer ===");
        System.out.println("Miembro: " + miembro.getNombre());
        
        // Mostrar quehaceres pendientes
        if (!miembro.getQuehaceres().isEmpty()) {
            Quehacer quehacer = miembro.getQuehaceres().get(0); // Tomar el primero como ejemplo
            System.out.println("Completando quehacer: " + quehacer.getNombre());
            
            // Usar el método según diagrama UML
            miembro.realizarQuehacer(quehacer);
            
            System.out.println("Quehacer completado exitosamente.");
        } else {
            System.out.println("No hay quehaceres pendientes para completar.");
        }
    }
    
    /**
     * Método para revisar quehaceres
     * Según diagrama UML
     */
    public void revisarQuehaceres() {
        System.out.println("=== INTERFAZ DEL MIEMBRO: Revisar Quehaceres ===");
        System.out.println("Miembro: " + miembro.getNombre());
        System.out.println("Quehaceres asignados: " + miembro.getQuehaceres().size());
        
        for (Quehacer quehacer : miembro.getQuehaceres()) {
            String estado = quehacer.estaCompletado() ? "COMPLETADO" : "PENDIENTE";
            System.out.println("- " + quehacer.getNombre() + " [" + estado + "]");
        }
    }
}