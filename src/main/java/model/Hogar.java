package model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Hogar {
    private static final Hogar instance = new Hogar();
    private List<MiembroHogar> miembros;

    private Hogar() {
        this.miembros = new ArrayList<>();
    }
    static Hogar getInstance() {
        return instance;
    }
    public void registrarMiembro(MiembroHogar miembro){
        this.miembros.add(miembro);
    }
    public void registrarQuehacer(String nombre, Dificultad d, int diasLimite) {
        Quehacer quehacer = new Quehacer(nombre, d, LocalDateTime.now().plusDays(diasLimite));
        if (miembros.isEmpty()) return;
        MiembroHogar miembroAsignado = miembros.get(0);
        for (MiembroHogar m : miembros) {
            if (m.getQuehaceresAsignados().size() < miembroAsignado.getQuehaceresAsignados().size()) {
                miembroAsignado = m;
            }
        }
        miembroAsignado.asignarQuehacer(quehacer);
        System.out.println(miembroAsignado.getNombre()+ " esta realizando la tarea" + miembroAsignado.getQuehaceresAsignados());
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

    public static void main(String[] args) {
        // 1. Se crea la instancia central del Hogar, que actuará como Sujeto.
        Hogar miHogar = Hogar.getInstance();
        // 2. Se crea al Jefe del Hogar, dándole una referencia al Hogar.
        JefeDelHogar jefe = new JefeDelHogar("Ana", 40);
        // 3. Se registra al Jefe como el primer miembro. Esto dispara la primera notificación.
        miHogar.registrarMiembro(jefe);
        // 4. El Jefe añade nuevos miembros. Cada llamada dispara una nueva notificación a todos los suscritos.
        jefe.organizarMiembro("Juan", 15);
        jefe.organizarMiembro("Lucía", 12);

        MiembroHogar sonia = new MiembroHogar("Sonia", 18);
        jefe.organizarMiembro(sonia);
        // prueba para ver que esten inscritos
        jefe.mostrarMiembrosRegistrados();
        //prueba para ver que se registran quehaceres
        jefe.organizarQuehaceres("Lavar los platos", Dificultad.FACIL, 1);
        jefe.organizarQuehaceres("Limpiar el baño", Dificultad.DIFICIL, 3);
        jefe.organizarQuehaceres("Pasear al perro", Dificultad.MEDIO, 0);
        jefe.organizarQuehaceres("Barrer la casa", Dificultad.FACIL, 0);
        jefe.organizarQuehaceres("Trapear el bano", Dificultad.MEDIO, 0);
        // --- Obtenemos referencias a los miembros ---
        MiembroHogar juan = miHogar.buscarMiembroPorNombre("Juan");
        MiembroHogar lucia = miHogar.buscarMiembroPorNombre("Lucía");
        MiembroHogar ana = miHogar.buscarMiembroPorNombre("Ana");
        System.out.println("\n====== INICIO DE LA SIMULACIÓN DE COMPLETAR TAREAS ======");

        // --- PRUEBA 1: Juan completa una tarea A TIEMPO ---
        if (!juan.getQuehaceresAsignados().isEmpty()) {
            Quehacer tareaDeJuan = juan.getQuehaceresAsignados().get(0);
            juan.registrarQuehacerCompleto(tareaDeJuan);
        }

        // --- PRUEBA 2: Lucía completa una tarea TARDE ---
        if (!lucia.getQuehaceresAsignados().isEmpty()) {
            Quehacer tareaDeLucia = lucia.getQuehaceresAsignados().get(0);
            lucia.registrarQuehacerCompleto(tareaDeLucia);
        }

        System.out.println("\n====== FIN DE LA SIMULACIÓN ======");
        System.out.println("Tareas restantes de Juan: " + juan.getQuehaceresAsignados().size());
        System.out.println("Tareas restantes de Lucía: " + lucia.getQuehaceresAsignados().size());
        System.out.println("Tareas restantes de Sonia: " + sonia.getQuehaceresAsignados().size());
        System.out.println("Tareas restantes de Ana: " + ana.getQuehaceresAsignados().size());
    }

}
