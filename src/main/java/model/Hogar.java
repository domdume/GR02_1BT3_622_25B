package model;
import java.util.ArrayList;
import java.util.List;

public class Hogar implements Sujeto{
    private static final Hogar instance = new Hogar();
    private List<MiembroHogar> miembros;
    private List<Observador> observadores;

    private Hogar() {
        this.miembros = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }
    static Hogar getInstance() {
        return instance;
    }

    public void registrarMiembro(MiembroHogar miembro){
        this.miembros.add(miembro);
        this.suscribir(miembro);
        this.notificar("¡" + miembro.getNombre() + " se ha unido a la familia!");
    }
    public List<MiembroHogar> getRegistroMiembro(){
        return miembros;
    }
    @Override
    public void suscribir(Observador observador) {
        this.observadores.add(observador);
    }

    @Override
    public void desuscribir(Observador observador) {
        this.observadores.remove(observador);
    }

    @Override
    public void notificar(String mensaje) {
        for (Observador observador : observadores) {
            observador.actualizar(mensaje);
        }
    }
    public List<MiembroHogar> getMiembros() {
        return miembros;
    }
    public static void main(String[] args) {
        // 1. Se crea la instancia central del Hogar, que actuará como Sujeto.
        Hogar miHogar = Hogar.getInstance();

        // 2. Se crea al Jefe del Hogar, dándole una referencia al Hogar.
        JefeDelHogar jefe = new JefeDelHogar("Ana", 40);

        // 3. Se registra al Jefe como el primer miembro. Esto dispara la primera notificación.
        miHogar.registrarMiembro(jefe);

        // 4. El Jefe añade nuevos miembros. Cada llamada dispara una nueva notificación a todos los suscritos.
        jefe.organizarMiembros("Juan", 15);
        jefe.organizarMiembros("Lucía", 12);
        // prueba para ver que esten inscritos
        jefe.mostrarMiembrosRegistrados();

    }
}
