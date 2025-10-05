package gui;

import model.Hogar;
import model.JefeDelHogar;
import model.MiembroHogar;
import model.Dificultad;

/**
 * Clase Boundary para la interfaz del Jefe del Hogar
 * Según diagrama UML - Package GUI
 */
public class InterfazDelJefe {
    
    private JefeDelHogar jefe;
    private Hogar hogar;
    
    public InterfazDelJefe(JefeDelHogar jefe) {
        this.jefe = jefe;
        this.hogar = Hogar.getInstance();
    }
    
    /**
     * Método para registrar un nuevo quehacer
     * Según diagrama UML
     */
    public void registrarUnQuehacer() {
        // Implementación básica para demostrar la trazabilidad
        System.out.println("=== INTERFAZ DEL JEFE: Registrar Quehacer ===");
        
        // Ejemplo de uso del patrón según el diagrama
        jefe.organizarQuehaceres("Lavar platos", 2);
        
        System.out.println("Quehacer registrado exitosamente.");
    }
    
    /**
     * Método para registrar un nuevo miembro
     * Según diagrama UML
     */
    public void registrarUnMiembro() {
        // Implementación básica para demostrar la trazabilidad
        System.out.println("=== INTERFAZ DEL JEFE: Registrar Miembro ===");
        
        // Ejemplo de uso del patrón según el diagrama
        jefe.organizarMiembro("Nuevo Miembro", 25);
        
        System.out.println("Miembro registrado exitosamente.");
    }
}