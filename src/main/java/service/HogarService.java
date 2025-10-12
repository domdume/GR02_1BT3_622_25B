package service;

import dao.MiembroHogarDAO;
import dao.QuehacerDAO;
import model.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service que coordina operaciones entre la capa de persistencia (DAOs)
 * y el modelo del dominio (Hogar singleton).
 *
 * Aplica refactorización "Extract Class" para centralizar lógica dispersa en servlets.
 */
public class HogarService {

    // Dependencias de la capa de persistencia
    private MiembroHogarDAO miembroDAO;
    private QuehacerDAO quehacerDAO;

    // Referencia al singleton del dominio
    private Hogar hogar;

    /**
     * Constructor - NO es singleton, cada servlet tendrá su instancia
     */
    public HogarService() {
        this.miembroDAO = new MiembroHogarDAO();
        this.quehacerDAO = new QuehacerDAO();
        this.hogar = Hogar.getInstance(); // Usa el singleton existente

        // Sincronizar al inicializar
        sincronizarConBD();

        System.out.println("[HogarService] Instancia creada y sincronizada");
    }

    /**
     * Método que coordinará la creación de miembros
     */
    public void organizarMiembro(String nombre, int edad, boolean esJefe) {
        System.out.println("[HogarService] organizarMiembro() llamado - nombre: " + nombre +
                ", edad: " + edad + ", esJefe: " + esJefe);
        System.out.println("TODO: Implementar en Move Method - por ahora delegando a DAO directo");

        MiembroHogar nuevoMiembro = new MiembroHogar();
        nuevoMiembro.setNombre(nombre);
        nuevoMiembro.setEdad(edad);
        miembroDAO.create(nuevoMiembro);

        sincronizarConBD();
    }

    /**
     * Método que coordinará la creación de quehaceres usando asignación automática
     * (implementación detallada en siguiente refactorización)
     */
    public void organizarQuehacer(String nombre, LocalDateTime tiempoLimite, Dificultad dificultad, Long miembroId) {
        MiembroHogar miembro = miembroDAO.findById(miembroId);
        Quehacer nuevoQuehacer = new Quehacer(nombre, tiempoLimite, dificultad);
        nuevoQuehacer.setMiembroHogar(miembro);
        quehacerDAO.create(nuevoQuehacer);
    }

    public JefeDelHogar obtenerJefeDelHogar() {
        return miembroDAO.findAll().stream()
                .filter(m -> m instanceof JefeDelHogar)
                .map(m -> (JefeDelHogar) m)
                .findFirst()
                .orElse(null);
    }

    public boolean yaExisteJefe() {
        return obtenerJefeDelHogar() != null;
    }

    /**
     * Sincroniza los datos entre la base de datos y la memoria (Hogar singleton)
     * Implementa patrón "Substitute Algorithm"
     */
    private void sincronizarConBD() {
        try {
            // 1. Cargar todos los miembros desde BD
            List<MiembroHogar> miembrosBD = miembroDAO.findAll();

            // 2. Limpiar la lista en memoria del singleton
            hogar.getRegistroMiembro().clear();

            // 3. Registrar cada miembro en el singleton
            for (MiembroHogar miembro : miembrosBD) {
                hogar.registrarMiembro(miembro);
            }

            System.out.println("[HogarService] Sincronización completada: " +
                    miembrosBD.size() + " miembros cargados en memoria");

        } catch (Exception e) {
            System.err.println("[HogarService] Error en sincronización: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida que los datos en BD y memoria estén sincronizados
     */
    private void validarConsistencia() {
        int miembrosBD = miembroDAO.findAll().size();
        int miembrosMemoria = hogar.getRegistroMiembro().size();

        if (miembrosBD != miembrosMemoria) {
            System.out.println("⚠️ [HogarService] Inconsistencia detectada - BD: " +
                    miembrosBD + ", Memoria: " + miembrosMemoria + " - Re-sincronizando...");
            sincronizarConBD();
        }
    }

    /**
     * Obtiene todos los miembros (delegación al DAO)
     */
    public List<MiembroHogar> obtenerTodosLosMiembros() {
        return miembroDAO.findAll();
    }

    /**
     * Obtiene todos los quehaceres (delegación al DAO)
     */
    public List<Quehacer> obtenerTodosLosQuehaceres() {
        return quehacerDAO.findAllWithMiembroHogar();
    }

    /**
     * Método de utilidad para debugging
     */
    public void mostrarEstadoHogar() {
        System.out.println("\n=== ESTADO DEL HOGAR ===");
        System.out.println("Miembros en BD: " + miembroDAO.findAll().size());
        System.out.println("Miembros en memoria: " + hogar.getRegistroMiembro().size());
        System.out.println("¿Existe jefe?: " + yaExisteJefe());
        if (yaExisteJefe()) {
            System.out.println("Jefe actual: " + obtenerJefeDelHogar().getNombre());
        }
        System.out.println("========================\n");
    }
}