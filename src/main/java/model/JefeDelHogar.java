package model;

import java.time.LocalDateTime;

public class JefeDelHogar extends MiembroHogar {

    public JefeDelHogar(String nombre, int edad) {
        super(nombre, edad);
    }
    public void organizarMiembro(String nombre, int edad){
        System.out.println("\n" + getNombre() + " está registrando a un nuevo miembro...");
        MiembroHogar nuevoMiembro = new MiembroHogar(nombre, edad);
        Hogar.getInstance().registrarMiembro(nuevoMiembro);
    }
    public void organizarMiembro(MiembroHogar miembro){
        System.out.println("\n" + getNombre() + " está registrando a un nuevo miembro...");
        Hogar.getInstance().registrarMiembro(miembro);
    }
    public void organizarQuehaceres(String nombre, int diasLimite ){
        System.out.println("\n" + getNombre() + " está asignando una nueva tarea...");
        // Usar dificultad MEDIO por defecto para mantener compatibilidad
        Quehacer nuevoQuehacer = new Quehacer(nombre, LocalDateTime.now().plusDays(diasLimite), Dificultad.MEDIO);
        Hogar.getInstance().registrarQuehacer(nuevoQuehacer);
    }

    public void mostrarMiembrosRegistrados() {
        System.out.println("\n--- Lista Actual de Miembros del Hogar ---");
        for (MiembroHogar miembro : Hogar.getInstance().getRegistroMiembro()) {
            System.out.println("- Nombre: " + miembro.getNombre() + ", Edad: " + miembro.getEdad());
        }
        System.out.println("----------------------------------------");
    }
}

