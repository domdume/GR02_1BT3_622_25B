package service;

import dao.MiembroHogarDAO;
import dao.QuehacerDAO;
import model.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.Objects;

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
     * Cada servlet tendrá su instancia
     */
    public HogarService() {
        this.miembroDAO = new MiembroHogarDAO();
        this.quehacerDAO = new QuehacerDAO();
        this.hogar = Hogar.getInstance(); // Usa el singleton existente

        //SUBSTITUTE ALGORITHM: Usar nuevo algoritmo de sincronización
        ResultadoSincronizacion resultado = sincronizarConBD();
        System.out.println("[HogarService] Instancia creada - " + resultado);
    }

    //Método que coordinará la creación de miembros
    public void organizarMiembro(String nombre, int edad, boolean esJefe) {
        try {
            // 1.USAR EXPLAINING VARIABLE ENCAPSULADA
            EstadoHogar estadoActual = analizarEstadoHogar();
            boolean usuarioQuiereJefe = esJefe;
            boolean esCreacionDeJefe = usuarioQuiereJefe && !estadoActual.existeJefe;
            boolean esPrimerMiembroQueSeraJefe = estadoActual.esPrimerMiembro;
            boolean debeUsarMetodoDelDominio = estadoActual.existeJefe && !usuarioQuiereJefe;

            EstadisticasHogar stats = obtenerEstadisticasHogar();
            System.out.println("[HogarService] Estado actual: " + estadoActual);
            System.out.println("[HogarService] Estadísticas: " + stats);
            System.out.println("[HogarService] Jefe actual: " + obtenerNombreJefeDelHogar());
            System.out.println("[HogarService] Decisión: crear jefe=" + esCreacionDeJefe +
                    ", usar dominio=" + debeUsarMetodoDelDominio);

            // 2. Determinar el tipo de miembro a crear usando variables explicativas
            if (esCreacionDeJefe) {
                // Crear nuevo jefe del hogar
                System.out.println("[HogarService] Creando nuevo jefe del hogar: " + nombre);
                JefeDelHogar nuevoJefe = new JefeDelHogar(nombre, edad);
                miembroDAO.create(nuevoJefe);
                hogar.registrarMiembro(nuevoJefe);

            } else if (esPrimerMiembroQueSeraJefe) {
                // Si es el primer miembro, automáticamente será jefe
                System.out.println("[HogarService] Primer miembro, creando como jefe: " + nombre);
                JefeDelHogar primerJefe = new JefeDelHogar(nombre, edad);
                miembroDAO.create(primerJefe);
                hogar.registrarMiembro(primerJefe);

            } else if (debeUsarMetodoDelDominio) {
                // Ya existe jefe, usar su método organizarMiembro()
                System.out.println("[HogarService] Usando método del dominio - JefeDelHogar.organizarMiembro()");
                estadoActual.jefeActual.organizarMiembro(nombre, edad); // ✅ USAR estadoActual.jefeActual

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
     * SUBSTITUTE ALGORITHM: Algoritmo robusto de sincronización BD-Memoria
     * Reemplaza el algoritmo básico con detección de conflictos y resolución automática
     */
    private ResultadoSincronizacion sincronizarConBD() {
        System.out.println("[HogarService] 🔄 Iniciando sincronización avanzada BD ↔ Memoria");

        try {
            // 1. ANÁLISIS INICIAL: Detectar tipo de inconsistencia
            TipoInconsistencia tipoDetectado = detectarTipoInconsistencia();

            if (tipoDetectado == TipoInconsistencia.NINGUNA) {
                System.out.println("[HogarService] ✅ Sistemas ya sincronizados");
                return new ResultadoSincronizacion(true, TipoInconsistencia.NINGUNA, 0, 0, 0,
                        "No se requiere sincronización");
            }

            System.out.println("[HogarService] ⚠️ Inconsistencia detectada: " + tipoDetectado.getDescripcion());

            // 2. SINCRONIZACIÓN BIDIRECCIONAL
            int miembrosSincronizados = sincronizarMiembros();
            int quehaceresSincronizados = sincronizarQuehaceres();
            int conflictosResueltos = resolverConflictosDeJefe();

            // 3. VALIDACIÓN FINAL
            TipoInconsistencia estadoFinal = detectarTipoInconsistencia();
            boolean exitoso = (estadoFinal == TipoInconsistencia.NINGUNA);

            String detalles = String.format("Tipo original: %s, Estado final: %s",
                    tipoDetectado.name(), estadoFinal.name());

            System.out.println("[HogarService] 🎯 Sincronización completada: " +
                    (exitoso ? "EXITOSA" : "CON PROBLEMAS"));

            return new ResultadoSincronizacion(exitoso, tipoDetectado,
                    miembrosSincronizados, quehaceresSincronizados, conflictosResueltos, detalles);

        } catch (Exception e) {
            System.err.println("[HogarService] ❌ Error crítico en sincronización: " + e.getMessage());
            e.printStackTrace();
            return new ResultadoSincronizacion(false, TipoInconsistencia.CANTIDAD_DIFERENTE,
                    0, 0, 0, "Error: " + e.getMessage());
        }
    }

    /**
     * Detecta el tipo específico de inconsistencia entre BD y memoria
     */
    private TipoInconsistencia detectarTipoInconsistencia() {
        List<MiembroHogar> miembrosBD = miembroDAO.findAll();
        List<MiembroHogar> miembrosMemoria = hogar.getRegistroMiembro();

        // Verificar cantidad
        if (miembrosBD.size() != miembrosMemoria.size()) {
            return TipoInconsistencia.CANTIDAD_DIFERENTE;
        }

        // Verificar IDs
        Set<Long> idsBD = miembrosBD.stream()
                .map(MiembroHogar::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> idsMemoria = miembrosMemoria.stream()
                .map(MiembroHogar::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!idsBD.equals(idsMemoria)) {
            return TipoInconsistencia.IDS_DIFERENTES;
        }

        // Verificar estado del jefe
        JefeDelHogar jefeBD = miembrosBD.stream()
                .filter(m -> m instanceof JefeDelHogar)
                .map(m -> (JefeDelHogar) m)
                .findFirst().orElse(null);
        JefeDelHogar jefeMemoria = miembrosMemoria.stream()
                .filter(m -> m instanceof JefeDelHogar)
                .map(m -> (JefeDelHogar) m)
                .findFirst().orElse(null);

        if ((jefeBD == null) != (jefeMemoria == null)) {
            return TipoInconsistencia.JEFE_INCONSISTENTE;
        }

        return TipoInconsistencia.NINGUNA;
    }

    /**
     * Sincroniza miembros entre BD y memoria con resolución de conflictos
     */
    private int sincronizarMiembros() {
        List<MiembroHogar> miembrosBD = miembroDAO.findAll();

        // Limpiar memoria y recargar desde BD (BD es fuente de verdad)
        hogar.getRegistroMiembro().clear();

        int sincronizados = 0;
        for (MiembroHogar miembro : miembrosBD) {
            try {
                hogar.registrarMiembro(miembro);
                sincronizados++;
            } catch (Exception e) {
                System.err.println("[HogarService] ⚠️ Error sincronizando miembro " +
                        miembro.getNombre() + ": " + e.getMessage());
            }
        }

        System.out.println("[HogarService] 👥 Miembros sincronizados: " + sincronizados);
        return sincronizados;
    }

    /**
     * Sincroniza quehaceres asegurando referencias correctas a miembros
     */
    private int sincronizarQuehaceres() {
        try {
            List<Quehacer> quehaceresBD = quehacerDAO.findAllWithMiembroHogar();

            // Verificar y corregir referencias de miembros en quehaceres
            int sincronizados = 0;
            for (Quehacer quehacer : quehaceresBD) {
                if (quehacer.getMiembroHogar() != null) {
                    // Buscar el miembro correspondiente en memoria
                    MiembroHogar miembroEnMemoria = hogar.getRegistroMiembro().stream()
                            .filter(m -> m.getId() != null &&
                                    m.getId().equals(quehacer.getMiembroHogar().getId()))
                            .findFirst().orElse(null);

                    if (miembroEnMemoria != null) {
                        // Actualizar referencia si es necesario
                        if (!quehacer.getMiembroHogar().equals(miembroEnMemoria)) {
                            quehacer.setMiembroHogar(miembroEnMemoria);
                            sincronizados++;
                        }
                    }
                }
            }

            System.out.println("[HogarService] 📋 Quehaceres sincronizados: " + sincronizados);
            return sincronizados;

        } catch (Exception e) {
            System.err.println("[HogarService] ⚠️ Error sincronizando quehaceres: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Resuelve conflictos específicos del jefe del hogar
     */
    private int resolverConflictosDeJefe() {
        try {
            JefeDelHogar jefeEnMemoria = hogar.getRegistroMiembro().stream()
                    .filter(m -> m instanceof JefeDelHogar)
                    .map(m -> (JefeDelHogar) m)
                    .findFirst().orElse(null);

            JefeDelHogar jefeEnBD = obtenerJefeDelHogar();

            if (jefeEnMemoria != null && jefeEnBD != null) {
                // Verificar que sean el mismo objeto
                if (!jefeEnMemoria.getId().equals(jefeEnBD.getId())) {
                    System.out.println("[HogarService] 🔧 Resolviendo conflicto de jefe del hogar");
                    // BD tiene precedencia, actualizar memoria
                    hogar.getRegistroMiembro().remove(jefeEnMemoria);
                    hogar.registrarMiembro(jefeEnBD);
                    return 1;
                }
            }

            return 0;

        } catch (Exception e) {
            System.err.println("[HogarService] ⚠️ Error resolviendo conflictos de jefe: " + e.getMessage());
            return 0;
        }
    }

    /**
     * SUBSTITUTE ALGORITHM: Validación avanzada de consistencia
     * Reemplaza validación básica con detección específica de inconsistencias
     */
    private void validarConsistencia() {
        ResultadoSincronizacion resultado = sincronizarConBD();

        if (!resultado.exitoso) {
            System.err.println("[HogarService] ❌ FALLO EN VALIDACIÓN: " + resultado.detalles);
        } else if (resultado.tipoInconsistencia != TipoInconsistencia.NINGUNA) {
            System.out.println("[HogarService] ✅ INCONSISTENCIA RESUELTA: " + resultado);
        } else {
            System.out.println("[HogarService] ✅ Sistemas consistentes");
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

    /**
     * INTRODUCIR EXPLAINING VARIABLE: Método helper que encapsula las variables explicativas
     * del estado del hogar para reutilización
     */
    public EstadoHogar analizarEstadoHogar() {
        //REPLACE TEMP WITH QUERY: Usar métodos de consulta en lugar de variables temporales
        EstadisticasHogar estadisticas = obtenerEstadisticasHogar();
        List<MiembroHogar> miembrosMemoria = hogar.getRegistroMiembro();
        boolean estaVacioMemoria = miembrosMemoria.isEmpty();
        boolean estaVacioBD = (estadisticas.totalMiembros == 0);
        boolean esPrimerMiembro = estaVacioMemoria && estaVacioBD;
        return new EstadoHogar(estadisticas.tieneJefe, esPrimerMiembro,
                obtenerJefeDelHogar(), (int)estadisticas.totalMiembros);
    }

    /**
     * ✅ SUBSTITUTE ALGORITHM: Algoritmo mejorado de sincronización BD-Memoria
     * Reemplaza sincronización básica con algoritmo robusto que detecta y resuelve conflictos
     */

    /**
     * Enum para clasificar tipos de inconsistencias
     */
    public enum TipoInconsistencia {
        NINGUNA("No hay inconsistencias"),
        CANTIDAD_DIFERENTE("Diferente cantidad de miembros"),
        IDS_DIFERENTES("Miembros con IDs diferentes"),
        DATOS_DIFERENTES("Datos de miembros inconsistentes"),
        JEFE_INCONSISTENTE("Estado del jefe del hogar inconsistente"),
        QUEHACERES_DESACTUALIZADOS("Quehaceres no sincronizados");

        private final String descripcion;

        TipoInconsistencia(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    /**
     * Clase que encapsula el resultado de una operación de sincronización
     */
    public static class ResultadoSincronizacion {
        public final boolean exitoso;
        public final TipoInconsistencia tipoInconsistencia;
        public final int miembrosSincronizados;
        public final int quehaceresSincronizados;
        public final int conflictosResueltos;
        public final String detalles;

        public ResultadoSincronizacion(boolean exitoso, TipoInconsistencia tipo,
                                       int miembros, int quehaceres, int conflictos, String detalles) {
            this.exitoso = exitoso;
            this.tipoInconsistencia = tipo;
            this.miembrosSincronizados = miembros;
            this.quehaceresSincronizados = quehaceres;
            this.conflictosResueltos = conflictos;
            this.detalles = detalles;
        }

        @Override
        public String toString() {
            return String.format("Sincronización{exitoso=%s, tipo=%s, miembros=%d, quehaceres=%d, conflictos=%d}",
                    exitoso, tipoInconsistencia.name(), miembrosSincronizados,
                    quehaceresSincronizados, conflictosResueltos);
        }
    }

    /**
     * REPLACE TEMP WITH QUERY: Métodos de consulta reutilizables
     * Eliminan variables temporales duplicadas y centralizan consultas comunes
     */

    /**
     * Consulta que retorna todos los miembros regulares (no jefes)
     */
    public List<MiembroHogar> obtenerMiembrosRegulares() {
        return miembroDAO.findAll().stream()
                .filter(m -> !(m instanceof JefeDelHogar))
                .collect(Collectors.toList());
    }

    /**
     * Consulta que retorna el nombre del jefe actual, o "Sin Jefe" si no existe
     */
    public String obtenerNombreJefeDelHogar() {
        JefeDelHogar jefe = obtenerJefeDelHogar();
        return jefe != null ? jefe.getNombre() : "Sin Jefe";
    }

    /**
     * Consulta que retorna estadísticas básicas del hogar
     */
    public EstadisticasHogar obtenerEstadisticasHogar() {
        List<MiembroHogar> todos = miembroDAO.findAll();
        long totalMiembros = todos.size();
        long miembrosRegulares = todos.stream()
                .filter(m -> !(m instanceof JefeDelHogar))
                .count();

        return new EstadisticasHogar(totalMiembros, miembrosRegulares, yaExisteJefe());
    }

    /**
     * Clase helper para estadísticas del hogar
     */
    public static class EstadisticasHogar {
        public final long totalMiembros;
        public final long miembrosRegulares;
        public final boolean tieneJefe;

        public EstadisticasHogar(long totalMiembros, long miembrosRegulares, boolean tieneJefe) {
            this.totalMiembros = totalMiembros;
            this.miembrosRegulares = miembrosRegulares;
            this.tieneJefe = tieneJefe;
        }

        @Override
        public String toString() {
            return String.format("EstadisticasHogar{total=%d, regulares=%d, tieneJefe=%s}",
                    totalMiembros, miembrosRegulares, tieneJefe);
        }
    }

    /**
     * Clase inner para encapsular el estado del hogar con variables explicativas
     */
    public static class EstadoHogar {
        public final boolean existeJefe;
        public final boolean esPrimerMiembro;
        public final JefeDelHogar jefeActual;
        public final int totalMiembros;

        public EstadoHogar(boolean existeJefe, boolean esPrimerMiembro, JefeDelHogar jefeActual, int totalMiembros) {
            this.existeJefe = existeJefe;
            this.esPrimerMiembro = esPrimerMiembro;
            this.jefeActual = jefeActual;
            this.totalMiembros = totalMiembros;
        }
    }
}