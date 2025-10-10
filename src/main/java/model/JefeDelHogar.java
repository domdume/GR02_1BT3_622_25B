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
        Hogar.getInstance().registrarMiembro(new MiembroHogar(nombre, edad));
    }
    public void organizarMiembro(MiembroHogar miembro){
        System.out.println("\n" + getNombre() + " está registrando a un nuevo miembro...");
        Hogar.getInstance().registrarMiembro(miembro);
    }
    public void organizarQuehaceres(String nombre, int diasLimite ){
        Hogar.getInstance().registrarQuehacer(new Quehacer(nombre, LocalDateTime.now().plusDays(diasLimite),
                Dificultad.MEDIO));
    }

    public void mostrarMiembrosRegistrados() {
        System.out.println("\n--- Lista Actual de Miembros del Hogar ---");
        for (MiembroHogar miembro : Hogar.getInstance().getRegistroMiembro()) {
            System.out.println("- Nombre: " + miembro.getNombre() + ", Edad: " + miembro.getEdad());
        }
        System.out.println("----------------------------------------");
        organizarMiembro("Juan", 20);
        organizarQuehaceres("Limpiar la casa", 10);
    }
}

