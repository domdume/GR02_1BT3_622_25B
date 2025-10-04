package model;
public class JefeDelHogar extends MiembroHogar {

//    private String rolFamiliar;
//    private RegistroMiembros registroMiembros;
    public JefeDelHogar(String nombre, int edad) {
        super(nombre, edad);
//        this.rolFamiliar = rolFamiliar;
//        this.registroMiembros = new RegistroMiembros();
//        this.registroMiembros.agregarMiembroConObligacion(this);

    }
    public void organizarMiembros(String nombre, int edad){
        System.out.println("\n" + getNombre() + " está registrando a un nuevo miembro...");
        MiembroHogar nuevoMiembro = new MiembroHogar(nombre, edad);
//        this.registroMiembros.agregarMiembroConObligacion(nuevoMiembro);
        Hogar.getInstance().registrarMiembro(nuevoMiembro);
    }
    public void organizarMiembros(MiembroHogar miembro){
        System.out.println("\n" + miembro.getNombre() + " está registrando a un nuevo miembro...");
        //this.registroMiembros.agregarMiembroConObligacion(miembro);
        Hogar.getInstance().registrarMiembro(miembro);
    }

    public void mostrarMiembrosRegistrados() {
        System.out.println("\n--- Lista Actual de Miembros del Hogar ---");
        // Pide la instancia del Hogar para obtener la lista de miembros.
        for (MiembroHogar miembro : Hogar.getInstance().getRegistroMiembro()) {
            System.out.println("- Nombre: " + miembro.getNombre() + ", Edad: " + miembro.getEdad());
        }
        System.out.println("----------------------------------------");
    }
}

