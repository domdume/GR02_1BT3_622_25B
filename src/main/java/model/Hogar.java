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
        jefe.organizarQuehaceres("Lavar los platos", 1);
        jefe.organizarQuehaceres("Limpiar el baño", 3);
        jefe.organizarQuehaceres("Pasear al perro", 0);
        jefe.organizarQuehaceres("Barrer la casa", 0);
        jefe.organizarQuehaceres("Trapear el bano", 0);
        // --- Obtenemos referencias a los miembros ---
        MiembroHogar juan = miHogar.buscarMiembroPorNombre("Juan");
        MiembroHogar lucia = miHogar.buscarMiembroPorNombre("Lucía");
        MiembroHogar ana = miHogar.buscarMiembroPorNombre("Ana");
        System.out.println("\n====== INICIO DE LA SIMULACIÓN DE COMPLETAR TAREAS ======");

        // --- PRUEBA 1: Juan completa una tarea A TIEMPO ---
        if (!juan.getQuehaceres().isEmpty()) {
            Quehacer tareaDeJuan = juan.getQuehaceres().get(0);
            juan.registrarQuehacerCompleto(tareaDeJuan);
        }

        // --- PRUEBA 2: Lucía completa una tarea TARDE ---
        if (!lucia.getQuehaceres().isEmpty()) {
            Quehacer tareaDeLucia = lucia.getQuehaceres().get(0);
            lucia.registrarQuehacerCompleto(tareaDeLucia);
        }

        // --- PRUEBA 3: Revisar quehaceres pendientes de Sonia
        //Recomendación. Primera mostrar la lista de miembros y de la lista escoger un miembro para revisar pendientes
                                            //Método a usar
        List<Quehacer> quehaceresPendientes = miHogar.getListaQuehaceresPendientes(juan);
        if (quehaceresPendientes == null) {
            System.out.println("No hay tareas pendientes");
        }else {
            System.out.println("Tareas pendientes de Sonia: " + quehaceresPendientes);
        }

        // --- PRUEBA 4: Revisar tipos de incentivos por miembro
        for(int i =0; i<miHogar.getRegistroMiembro().size();i++){
            System.out.println(miHogar.getRegistroMiembro().get(i).getNombre());
            if(miHogar.getRegistroMiembro().get(i).getIncentivos().isEmpty()){
                System.out.println("\tNo tiene incentivos");
            }
            for(int j =0; j< miHogar.getRegistroMiembro().get(i).getIncentivos().size();j++){
                System.out.println("\t" + miHogar.getRegistroMiembro().get(i).getIncentivos().get(j).getTipoIncentivo());
            }
        }


        System.out.println("\n====== FIN DE LA SIMULACIÓN ======");
        System.out.println("Tareas restantes de Juan: " + juan.getQuehaceres().size());
        System.out.println("Tareas restantes de Lucía: " + lucia.getQuehaceres().size());
        System.out.println("Tareas restantes de Sonia: " + sonia.getQuehaceres().size());
        System.out.println("Tareas restantes de Ana: " + ana.getQuehaceres().size());
    }

    private List<Quehacer> getListaQuehaceresPendientes(MiembroHogar miembro) {
        //verificar si existe el objeto
        if(miembro == null){
            System.out.println("El miembro no existe...");
            return null;
        }
        //existe el miembro registrado
        if(!miembros.contains(miembro)){
            System.out.println("El miembro no existe en el registro");
            return null;
        }
        if(miembro.getQuehaceres().isEmpty()){
            System.out.println("No hay tareas pendientes");
            return null;
        }
        return miembro.getQuehaceres();
    }

}
