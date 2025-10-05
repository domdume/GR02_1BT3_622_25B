package model;

import java.time.LocalDateTime;

public class JefeDelHogar extends MiembroHogar {
    private RegistroMiembros registroMiembros;
    private RegistroQuehacer registroQuehacer;
    public JefeDelHogar(String nombre, int edad) {
        super(nombre, edad);
       this.registroMiembros = new RegistroMiembros();
       this.registroQuehacer = new RegistroQuehacer();
    }
    public void organizarMiembro(String nombre, int edad){
        System.out.println("\n" + getNombre() + " está registrando a un nuevo miembro...");
        MiembroHogar nuevoMiembro = new MiembroHogar(nombre, edad);
        registroMiembros.agregarMiembroConObligacion(nuevoMiembro);
        registroQuehacer.suscribir(nuevoMiembro);
        Hogar.getInstance().registrarMiembro(nuevoMiembro);
    }
    public void organizarMiembro(MiembroHogar miembro){
        System.out.println("\n" + getNombre() + " está registrando a un nuevo miembro...");
        registroMiembros.agregarMiembroConObligacion(miembro);
        registroQuehacer.suscribir(miembro);
        Hogar.getInstance().registrarMiembro(miembro);
    }
    public void organizarQuehaceres(String nombre, Dificultad d, int diasLimite ){
        System.out.println("\n" + getNombre() + " está asigno una nueva tarea...");
        Hogar.getInstance().registrarQuehacer(nombre, d, diasLimite);
        Quehacer nuevoQuehacer = new Quehacer(nombre,d, LocalDateTime.now().plusDays(diasLimite));
        registroQuehacer.agregarQuehacer(nuevoQuehacer);
    }

    public void mostrarMiembrosRegistrados() {
        System.out.println("\n--- Lista Actual de Miembros del Hogar ---");
        for (MiembroHogar miembro : Hogar.getInstance().getRegistroMiembro()) {
            System.out.println("- Nombre: " + miembro.getNombre() + ", Edad: " + miembro.getEdad());
        }
        System.out.println("----------------------------------------");
    }
}

