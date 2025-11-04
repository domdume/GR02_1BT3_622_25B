package service;

import model.MiembroHogar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//public class EmblemaService implements IEmblemaService {
//    // Usamos un Map para almacenar los emblemas de cada miembro
//    private Map<Long, Set<String>> emblemaPorMiembro;
//
//    public EmblemaService() {
//        this.emblemaPorMiembro = new HashMap<>();
//    }
//
//    /**
//     * Asigna el emblema de Aprendiz a un miembro cuando gana su primera liga
//     * @param miembro El miembro que ganó su primera liga
//     * @return true si se asignó el emblema, false si ya lo tenía
//     */
//    public boolean asignarEmblemaAprendiz(MiembroHogar miembro) {
//        if (miembro == null || miembro.getId() == null) {
//            throw new IllegalArgumentException("Miembro no puede ser nulo y debe tener ID");
//        }
//
//        // Verificar que el miembro tenga una liga (ha ganado su primera liga)
//        if (miembro.getLiga() == null) {
//            return false; // No ha ganado ninguna liga todavía
//        }
//
//        // Obtener o crear el conjunto de emblemas del miembro
//        Set<String> emblemasMiembro = emblemaPorMiembro.computeIfAbsent(
//            miembro.getId(),
//            k -> new HashSet<>()
//        );
//
//        // Verificar si ya tiene el emblema
//        if (emblemasMiembro.contains("APRENDIZ")) {
//            return false;
//        }
//
//        // Asignar el emblema
//        emblemasMiembro.add("APRENDIZ");
//        return true;
//    }
//
//    @Override
//    public boolean asignarEmblemaAprendiz(Long miembroId) {
//        if (miembroId == null) return false;
//        Set<String> emblemasMiembro = emblemaPorMiembro.computeIfAbsent(miembroId, k -> new HashSet<>());
//        if (emblemasMiembro.contains("APRENDIZ")) return false;
//        emblemasMiembro.add("APRENDIZ");
//        return true;
//    }
//
//    @Override
//    public boolean asignarEmblemaAscenso(Long miembroId, String ligaAnterior, String ligaNueva) {
//        if (miembroId == null || ligaNueva == null) return false;
//        String nombre = "ASCENSO_" + (ligaAnterior != null ? ligaAnterior : "N/A") + "_TO_" + ligaNueva;
//        Set<String> emblemasMiembro = emblemaPorMiembro.computeIfAbsent(miembroId, k -> new HashSet<>());
//        if (emblemasMiembro.contains(nombre)) return false;
//        emblemasMiembro.add(nombre);
//        return true;
//    }
//
//    /**
//     * Verifica si un miembro tiene un emblema específico
//     * @param miembroId ID del miembro
//     * @param emblema Nombre del emblema a verificar
//     * @return true si tiene el emblema, false si no
//     */
//    public boolean tieneEmblema(Long miembroId, String emblema) {
//        if (miembroId == null || emblema == null) {
//            return false;
//        }
//
//        Set<String> emblemasMiembro = emblemaPorMiembro.get(miembroId);
//        return emblemasMiembro != null && emblemasMiembro.contains(emblema);
//    }
//
//    /**
//     * Obtiene todos los emblemas de un miembro
//     * @param miembroId ID del miembro
//     * @return Set con los nombres de los emblemas del miembro
//     */
//    public Set<String> obtenerEmblemas(Long miembroId) {
//        return emblemaPorMiembro.getOrDefault(miembroId, new HashSet<>());
//    }
//}


public class EmblemaService implements IEmblemaService {
    // 1. Crear una única instancia estática y privada
    private static final EmblemaService instancia = new EmblemaService();

    // Usamos un Map para almacenar los emblemas de cada miembro
    private final Map<Long, Set<String>> emblemaPorMiembro;

    // 2. Hacer el constructor privado para que no se pueda instanciar desde fuera
    private EmblemaService() {
        this.emblemaPorMiembro = new HashMap<>();
    }

    // 3. Proveer un método público y estático para obtener la única instancia
    public static EmblemaService getInstancia() {
        return instancia;
    }

    /**
     * Asigna el emblema de Aprendiz a un miembro cuando gana su primera liga
     * @param miembro El miembro que ganó su primera liga
     * @return true si se asignó el emblema, false si ya lo tenía
     */
    public boolean asignarEmblemaAprendiz(MiembroHogar miembro) {
        if (miembro == null || miembro.getId() == null) {
            throw new IllegalArgumentException("Miembro no puede ser nulo y debe tener ID");
        }
        // Verificar que el miembro tenga una liga (ha ganado su primera liga)
        if (miembro.getLiga() == null) {
            return false; // No ha ganado ninguna liga todavía
        }
        return asignarEmblemaAprendiz(miembro.getId());
    }

    @Override
    public boolean asignarEmblemaAprendiz(Long miembroId) {
        if (miembroId == null) return false;
        Set<String> emblemasMiembro = emblemaPorMiembro.computeIfAbsent(miembroId, k -> new HashSet<>());
        // add devuelve true si el elemento no existía y se añadió con éxito
        return emblemasMiembro.add("APRENDIZ");
    }

    @Override
    public boolean asignarEmblemaAscenso(Long miembroId, String ligaAnterior, String ligaNueva) {
        if (miembroId == null || ligaNueva == null) return false;
        String nombre = "ASCENSO_" + (ligaAnterior != null ? ligaAnterior.toUpperCase() : "N/A") + "_TO_" + ligaNueva.toUpperCase();
        Set<String> emblemasMiembro = emblemaPorMiembro.computeIfAbsent(miembroId, k -> new HashSet<>());
        return emblemasMiembro.add(nombre);
    }

    /**
     * Verifica si un miembro tiene un emblema específico
     * @param miembroId ID del miembro
     * @param emblema Nombre del emblema a verificar
     * @return true si tiene el emblema, false si no
     */
    public boolean tieneEmblema(Long miembroId, String emblema) {
        if (miembroId == null || emblema == null) {
            return false;
        }
        Set<String> emblemasMiembro = emblemaPorMiembro.get(miembroId);
        return emblemasMiembro != null && emblemasMiembro.contains(emblema);
    }

    /**
     * Obtiene todos los emblemas de un miembro
     * @param miembroId ID del miembro
     * @return Set con los nombres de los emblemas del miembro
     */
    public Set<String> obtenerEmblemas(Long miembroId) {
        return emblemaPorMiembro.getOrDefault(miembroId, new HashSet<>());
    }
}