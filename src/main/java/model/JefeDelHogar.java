package model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("JefeDelHogar")
public class JefeDelHogar extends MiembroHogar {

    // Constructor vacío requerido por JPA
    public JefeDelHogar() {
        super();
    }

    public JefeDelHogar(String nombre, int edad) {
        super(nombre, edad);
    }
    public void organizarMiembro(String nombre, int edad){
        System.out.println("\n" + getNombre() + " está registrando a un nuevo miembro...");
        MiembroHogar nuevoMiembro = new MiembroHogar(nombre, edad);
        nuevoMiembro.registrarMiembro(Hogar.getInstance());
    }
    public void organizarMiembro(MiembroHogar miembro){
        System.out.println("\n" + getNombre() + " está registrando a un nuevo miembro...");
        miembro.registrarMiembro(Hogar.getInstance());
    }
    public void organizarQuehaceres(String nombre, int diasLimite ){
        System.out.println("\n" + getNombre() + " está asignando una nueva tarea...");
        // Usar dificultad MEDIO por defecto para mantener compatibilidad
        Quehacer nuevoQuehacer = new Quehacer(nombre, LocalDateTime.now().plusDays(diasLimite), Dificultad.MEDIO);
        nuevoQuehacer.registrarQuehacer(Hogar.getInstance());
    }

    public void mostrarMiembrosRegistrados() {
        System.out.println("\n--- Lista Actual de Miembros del Hogar ---");
        for (MiembroHogar miembro : Hogar.getInstance().getRegistroMiembro()) {
            System.out.println("- Nombre: " + miembro.getNombre() + ", Edad: " + miembro.getEdad());
        }
        System.out.println("----------------------------------------");
    }
}

