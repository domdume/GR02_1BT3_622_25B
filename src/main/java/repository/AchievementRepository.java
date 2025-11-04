package repository;

public interface AchievementRepository {
    boolean tieneLogro(Long miembroId, String logroId);
    boolean tieneCualquierLogro(Long miembroId);
    void guardarLogro(Long miembroId, String logroId);
    int obtenerTareasCompletadas(Long miembroId);
    void incrementarContadorTareas(Long miembroId);
}
