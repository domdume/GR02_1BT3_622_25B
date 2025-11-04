package repository;

public interface AchievementRepository {
    void guardarLogro(Long miembroId, String codigoLogro);
    boolean tieneLogro(Long miembroId, String codigoLogro);
}
