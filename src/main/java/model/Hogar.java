package model;
import java.util.ArrayList;
import java.util.Comparator;
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
    public void organizarQuehacer(Quehacer quehacer) {
        if (miembros.isEmpty()) return;
        MiembroHogar miembroAsignado = obtenerMiembroConMenosCarga();
        miembroAsignado.asignarQuehacer(quehacer);
    }

    private MiembroHogar obtenerMiembroConMenosCarga() {
        return miembros.stream()
                .min(Comparator.comparingInt(m -> m.getQuehaceres().size()))
                .orElse(null);
    }

    public List<MiembroHogar> getRegistroMiembro(){
        return miembros;
    }
}
