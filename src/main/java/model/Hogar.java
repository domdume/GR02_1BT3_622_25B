package model;
import java.util.ArrayList;
import java.util.List;

public class Hogar {
    private static final Hogar instance = new Hogar();
    public List<MiembroHogar> miembros;

    private Hogar() {
        this.miembros = new ArrayList<>();
    }
    public static Hogar getInstance() {
        return instance;
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
