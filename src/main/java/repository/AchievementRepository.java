package repository;

public interface AchievementRepository {
    boolean tieneLogro(Long miembroId, String logroId);
    void guardarLogro(Long miembroId, String logroId);
    int obtenerTareasCompletadas(Long miembroId);
    void incrementarContadorTareas(Long miembroId);
}
