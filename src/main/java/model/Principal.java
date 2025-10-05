package model; // ¡Necesitas esta clase!

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Principal {

    // Lista de Miembros QUEMADOS (Hardcoded)
   // static RegistroMiembros dbRegistro = new RegistroMiembros();
    //static RegistroQuehacer dbQuehaceres = new RegistroQuehacer();
}// ¡Instancia de RegistroQuehacer!
//}
//    public static void inicializarMiembrosQuemados() {
//        dbRegistro.agregarMiembroConObligacion(new MiembroHogar("Juan", 16));
//        dbRegistro.agregarMiembroConObligacion(new MiembroHogar("María",20));
//        dbRegistro.agregarMiembroConObligacion(new MiembroHogar("Pedro",23));
//
//
//        // Opcional: Si quieres que los miembros reciban notificaciones, suscríbelos aquí.
//        dbQuehaceres.suscribir(new MiembroHogar("Juan",16));
//        dbQuehaceres.suscribir(new MiembroHogar("María",20));
//        dbQuehaceres.suscribir(new MiembroHogar("Pedro",23));
//
//        System.out.println("✅ Miembros inicializados: Juan, María, Pedro.");
//    }
//
//     --- LÓGICA CLAVE: ASIGNACIÓN POR CONSOLA (AHORA USA dbQuehaceres) ---
//    public static void asignarQuehacerPorConsola(Scanner scanner) {
//        System.out.println("\n--- 📝 ASIGNAR NUEVO QUEHACER ---");
//
//        // 1. Mostrar Miembros disponibles
//        System.out.println("Miembros disponibles: " + dbRegistro.getMiembros().stream().map(MiembroHogar::getNombre).toList());
//
//        // 2. IDENTIFICAR AL MIEMBRO
//        System.out.print("Asignar a (nombre): ");
//        String nombreMiembro = scanner.nextLine();
//        MiembroHogar miembro = dbRegistro.buscarMiembroPorNombre(nombreMiembro);
//
//        if (miembro == null) {
//            System.out.println("❌ Miembro '" + nombreMiembro + "' no encontrado. Intente con Juan, María o Pedro.");
//            return;
//        }
//
//        // 3. CAPTURAR DETALLES DE LA TAREA
//        System.out.println("\n-> Creando Tarea...");
//        System.out.print("Nombre del quehacer: ");
//        String nombreQuehacer = scanner.nextLine();
//
//        Dificultad dificultad = obtenerDificultad(scanner);
//
//        System.out.print("Límite de días (Ej: 3): ");
//        int diasLimite = 0;
//        try {
//            diasLimite = Integer.parseInt(scanner.nextLine());
//        } catch (NumberFormatException e) {
//            diasLimite = 1;
//        }
//
//        LocalDateTime tiempoLimite = LocalDateTime.now().plusDays(diasLimite);
//
//        // 4. CREAR QUEHACER
//        Quehacer nuevoQuehacer = new Quehacer(nombreQuehacer, dificultad, tiempoLimite);
//
//        // 5. REGISTRAR GLOBALMENTE (USA RegistroQuehacer y Notifica a Observadores)
//        dbQuehaceres.agregarQuehacer(nuevoQuehacer);
//
//        // 6. ASIGNAR ESPECÍFICAMENTE AL MIEMBRO (Lógica de asignación)
//        miembro.asignarQuehacer(nuevoQuehacer);
//        System.out.println("✅ Tarea asignada: " + nuevoQuehacer.getNombre() + " fue asignada a " + miembro.getNombre() + ".");
//
//        // 7. NOTIFICACIÓN ESPECÍFICA AL MIEMBRO ASIGNADO (¡Esta es la clave!)
//        String notificacionPersonal = "🎉 ¡Nueva Tarea Asignada! Debes realizar: " + nuevoQuehacer.getNombre();
//        miembro.actualizar(notificacionPersonal); // Llama al método del miembro
//    }
//
//    // --- LÓGICA DE LISTADO (Se mantiene igual) ---
//
//    public static void mostrarQuehaceresDeMiembro(Scanner scanner) {
//        System.out.println("\n--- 📋 VER QUEHACERES ASIGNADOS ---");
//
//        System.out.print("Ingrese el nombre del Miembro para ver sus tareas: ");
//        String nombreMiembro = scanner.nextLine();
//        MiembroHogar miembro = dbRegistro.buscarMiembroPorNombre(nombreMiembro);
//
//        if (miembro == null) {
//            System.out.println("❌ Miembro '" + nombreMiembro + "' no encontrado.");
//            return;
//        }
//
//        List<Quehacer> listaTareas = miembro.getQuehaceresAsignados();
//
//        System.out.println("\nQuehaceres pendientes de " + miembro.getNombre() + " (" + listaTareas.size() + " tareas):");
//
//        if (listaTareas.isEmpty()) {
//            System.out.println("   (No hay tareas asignadas).");
//        } else {
//            for (int i = 0; i < listaTareas.size(); i++) {
//                Quehacer q = listaTareas.get(i);
//                System.out.printf("   %d. %s [Dificultad: %s, Límite: %s]\n",
//                        i + 1,
//                        q.getNombre(),
//                        q.getDificultad(),
//                        q.getTiempoLimite().toLocalDate()
//                );
//            }
//        }
//    }
//
//    // --- FUNCIÓN DE UTILIDAD (Obtener Dificultad) ---
//    private static Dificultad obtenerDificultad(Scanner scanner) {
//        Dificultad dificultad = null;
//        while (dificultad == null) {
//            System.out.println("Dificultades (1: FACIL, 2: MEDIO, 3: DIFICIL): ");
//            System.out.print("Ingrese opción de dificultad: ");
//            String opcion = scanner.nextLine().trim();
//
//            switch (opcion) {
//                case "1": dificultad = Dificultad.FACIL; break;
//                case "2": dificultad = Dificultad.MEDIO; break;
//                case "3": dificultad = Dificultad.DIFICIL; break;
//                default: System.out.println("⚠️ Opción no válida.");
//            }
//        }
//        return dificultad;
//    }
//
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        inicializarMiembrosQuemados();
//
//        // ... (código del menú principal) ...
//        int opcion = -1;
//        while (opcion != 0) {
//            System.out.println("\n=================================");
//            System.out.println("1. Asignar Nuevo Quehacer a un Miembro");
//            System.out.println("2. Ver Quehaceres de un Miembro");
//            System.out.println("0. Salir");
//            System.out.print("Seleccione una opción: ");
//
//            if (scanner.hasNextInt()) {
//                opcion = scanner.nextInt();
//                scanner.nextLine();
//            } else {
//                scanner.nextLine();
//                opcion = -1;
//            }
//
//            switch (opcion) {
//                case 1:
//                    asignarQuehacerPorConsola(scanner);
//                    break;
//                case 2:
//                    mostrarQuehaceresDeMiembro(scanner);
//                    break;
//                case 0:
//                    System.out.println("Saliendo del sistema. ¡Adiós!");
//                    break;
//                default:
//                    System.out.println("Opción no válida.");
//            }
//        }
//
//        scanner.close();
//    }
//}