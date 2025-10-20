package repository;

public interface AchievementRepository {

    //Verifica si un miembro tiene un logro
    boolean tieneLogro(Long miembroId, String logroId);

    //Guarda un logro para un miembro
    void guardarLogro(Long miembroId, String logroId);
}
