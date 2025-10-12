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
        System.out.println("[HogarService] organizarMiembro() - " + nombre + ", edad: " + edad + ", esJefe: " + esJefe);

        try {
            // 1. Verificar si ya existe un jefe
            JefeDelHogar jefeActual = obtenerJefeDelHogar();
            boolean esPrimerMiembro = hogar.getRegistroMiembro().isEmpty() && miembroDAO.findAll().isEmpty();

            // 2. Determinar el tipo de miembro a crear
            if (esJefe && jefeActual == null) {
                // Crear nuevo jefe del hogar
                System.out.println("[HogarService] Creando nuevo jefe del hogar: " + nombre);
                JefeDelHogar nuevoJefe = new JefeDelHogar(nombre, edad);
                miembroDAO.create(nuevoJefe);
                hogar.registrarMiembro(nuevoJefe);

            } else if (esPrimerMiembro) {
                // Si es el primer miembro, automáticamente será jefe
                System.out.println("[HogarService] Primer miembro, creando como jefe: " + nombre);
                JefeDelHogar primerJefe = new JefeDelHogar(nombre, edad);
                miembroDAO.create(primerJefe);
                hogar.registrarMiembro(primerJefe);

            } else if (jefeActual != null) {
                // Ya existe jefe, usar su método organizarMiembro()
                System.out.println("[HogarService] Usando método del dominio - JefeDelHogar.organizarMiembro()");
                jefeActual.organizarMiembro(nombre, edad);

                // Buscar el miembro recién creado en memoria y persistirlo
                MiembroHogar miembroCreado = hogar.buscarMiembroPorNombre(nombre);
                if (miembroCreado != null) {
                    miembroDAO.create(miembroCreado);
                }

            } else {
                // Caso excepcional: crear miembro regular
                System.out.println("[HogarService] Creando miembro regular: " + nombre);
                MiembroHogar nuevoMiembro = new MiembroHogar(nombre, edad);
                miembroDAO.create(nuevoMiembro);
                hogar.registrarMiembro(nuevoMiembro);
            }

            // 3. Validar consistencia final
            validarConsistencia();

            System.out.println("[HogarService] Miembro creado exitosamente: " + nombre);

        } catch (Exception e) {
            System.err.println("[HogarService] Error al organizar miembro: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al crear miembro: " + e.getMessage(), e);
        }
    }

    /**
     * Método que coordina la creación de quehaceres usando la lógica del dominio UML
     * Aplica "Move Method" desde QuehacerServlet hacia HogarService
     */
    public void organizarQuehacer(String nombre, LocalDateTime tiempoLimite, Dificultad dificultad, String miembroIdOpcional) {
        System.out.println("[HogarService] organizarQuehacer() - " + nombre + ", miembroId: " + miembroIdOpcional);
        
        try {
            // Crear el quehacer
            Quehacer nuevoQuehacer = new Quehacer(nombre, tiempoLimite, dificultad);
            
            // Decidir el tipo de asignación
            if (miembroIdOpcional == null || miembroIdOpcional.trim().isEmpty()) {
                // ✅ USAR LÓGICA DEL DOMINIO - Asignación automática
                System.out.println("[HogarService] Usando asignación automática del dominio");
                hogar.registrarQuehacer(nuevoQuehacer);
                
                // El quehacer ya está asignado por Hogar.registrarQuehacer()
                System.out.println("[HogarService] Quehacer asignado automáticamente a: " + 
                    (nuevoQuehacer.getMiembroHogar() != null ? nuevoQuehacer.getMiembroHogar().getNombre() : "ninguno"));
                
            } else {
                // Asignación manual específica
                System.out.println("[HogarService] Usando asignación manual a miembro específico");
                Long miembroId = Long.parseLong(miembroIdOpcional);
                MiembroHogar miembroAsignado = miembroDAO.findById(miembroId);
                
                if (miembroAsignado == null) {
                    throw new RuntimeException("No se encontró miembro con ID: " + miembroId);
                }
                
                // Usar el método del dominio para asignar
                miembroAsignado.asignarQuehacer(nuevoQuehacer);
                
                // Asegurar que el miembro esté sincronizado en memoria
                if (!hogar.getRegistroMiembro().contains(miembroAsignado)) {
                    hogar.registrarMiembro(miembroAsignado);
                }
                
                System.out.println("[HogarService] Quehacer asignado manualmente a: " + miembroAsignado.getNombre());
            }
            
            // Persistir en base de datos
            quehacerDAO.create(nuevoQuehacer);
            
            // Validar consistencia
            validarConsistencia();
            
            System.out.println("[HogarService] Quehacer creado exitosamente: " + nombre);
            
        } catch (Exception e) {
            System.err.println("[HogarService] Error al organizar quehacer: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al crear quehacer: " + e.getMessage(), e);
        }
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

    //Obtiene todos los miembros (delegación al DAO)
    public List<MiembroHogar> obtenerTodosLosMiembros() {
        return miembroDAO.findAll();
    }

    //Obtiene todos los quehaceres (delegación al DAO)
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