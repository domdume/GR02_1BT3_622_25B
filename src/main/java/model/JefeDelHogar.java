package model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("JefeDelHogar")
public class JefeDelHogar extends MiembroHogar {
    @Override
    public boolean getEsJefe() {
        return true;
    }

    // Constructor vacío requerido por JPA
    public JefeDelHogar() {
        super();
    }

    public JefeDelHogar(String nombre, int edad) {
        super(nombre, edad);
    }
    public void organizarMiembro(String nombre, int edad){
        // Solo persistir el nuevo miembro, nunca el propio jefe
        MiembroHogar nuevoMiembro = new MiembroHogar(nombre, edad);
        new dao.MiembroHogarDAO().create(nuevoMiembro);
        Hogar.getInstance().registrarMiembro(nuevoMiembro);
    }
//    public void organizarMiembro(MiembroHogar miembro){
//        System.out.println("\n" + getNombre() + " está registrando a un nuevo miembro...");
//        Hogar.getInstance().registrarMiembro(miembro);
//    }
//    public void registrarQuehacer(String nombre, int diasLimite ) {
//        Hogar.getInstance().registrarQuehacer(new Quehacer(nombre, LocalDateTime.now().plusDays(diasLimite), Dificultad.MEDIO));
//    }

    public void mostrarMiembrosRegistrados() {
        System.out.println("\n--- Lista Actual de Miembros del Hogar ---");
        for (MiembroHogar miembro : Hogar.getInstance().getRegistroMiembro()) {
            System.out.println("- Nombre: " + miembro.getNombre() + ", Edad: " + miembro.getEdad());
        }
        System.out.println("----------------------------------------");
    }
}

