package service;

/**
 * Interfaz para el manejo de logros/emblemas.
 * Abstrae la lógica de asignación de emblemas para facilitar testing.
 */
public interface IEmblemaService {
    /**
     * Asigna un emblema a un miembro por ascenso de liga.
     * @param miembroId ID del miembro que ascendió
     * @param ligaAnterior Liga desde la que ascendió
     * @param ligaNueva Liga a la que ascendió
     * @return true si el emblema fue asignado, false si ya lo tenía o no aplica
     */
    boolean asignarEmblemaAscenso(Long miembroId, String ligaAnterior, String ligaNueva);

    /**
     * Asigna el emblema de Aprendiz a un miembro cuando gana su primera liga.
     * @param miembroId ID del miembro que ganó su primera liga
     * @return true si el emblema fue asignado, false si ya lo tenía o no aplica
     */
    boolean asignarEmblemaAprendiz(Long miembroId);

}