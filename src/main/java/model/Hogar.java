package model;
import java.util.ArrayList;
import java.util.List;

public class Hogar {
    private static final Hogar instance = new Hogar();
    private List<MiembroHogar> miembros;

    private Hogar() {
        this.miembros = new ArrayList<>();
    }
    public static Hogar getInstance() {
        return instance;
    }
    public void registrarMiembro(MiembroHogar miembro){
        this.miembros.add(miembro);
    }
    public void registrarQuehacer(Quehacer quehacer) {
        if (miembros.isEmpty()) return;
        MiembroHogar miembroAsignado = miembros.get(0);
        for (MiembroHogar m : miembros) {
            if (m.getQuehaceres().size() < miembroAsignado.getQuehaceres().size()) {
                miembroAsignado = m;
            }
        }
        miembroAsignado.asignarQuehacer(quehacer);
        System.out.println(miembroAsignado.getNombre()+ " esta realizando la tarea" + miembroAsignado.getQuehaceres());
    }
    public List<MiembroHogar> getRegistroMiembro(){
        return miembros;
    }
    public MiembroHogar buscarMiembroPorNombre(String nombre) {
        for (MiembroHogar miembro : miembros) {
            if (miembro.getNombre().equalsIgnoreCase(nombre.trim())) {
                return miembro;
            }
        }
        return null; // Retorna null si no lo encuentra
    }



}
