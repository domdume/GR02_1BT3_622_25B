package repository;

import model.LogroMiembro;
import model.TipoMedalla;
import model.TipoLogro;
import model.MiembroHogar;
import util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class JpaAchievementRepository implements AchievementRepository {

    @Override
    public boolean tieneLogro(Long miembroId, String logroId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(l) FROM LogroMiembro l WHERE l.miembro.id = :miembroId AND l.logroId = :logroId",
                    Long.class)
                    .setParameter("miembroId", miembroId)
                    .setParameter("logroId", logroId)
                    .getSingleResult() > 0;
        } catch (NoResultException e) {
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public void guardarLogro(Long miembroId, String logroId) {
        if (!tieneLogro(miembroId, logroId)) {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                em.getTransaction().begin();
                MiembroHogar miembro = em.find(MiembroHogar.class, miembroId);
                if (miembro != null) {
                    LogroMiembro logroMiembro = new LogroMiembro(miembro, logroId, obtenerTipoLogro(logroId), obtenerNivel(logroId));
                    em.persist(logroMiembro);
                    em.getTransaction().commit();
                }
            } finally {
                em.close();
            }
        }
    }

    private TipoLogro obtenerTipoLogro(String logroId) {
        if (logroId.startsWith("TAREAS_")) {
            return TipoLogro.MEDALLA;
        } else if (logroId.startsWith("STREAK_")) {
            return TipoLogro.EMBLEMA;
        } else if (logroId.startsWith("POINTS_")) {
            return TipoLogro.INSIGNIA;
        } else {
            return TipoLogro.TROFEO;
        }
    }

    private TipoMedalla obtenerNivel(String logroId) {
        if (logroId.equals("TAREAS_5") || logroId.startsWith("STREAK_3")) {
            return TipoMedalla.NINGUNA;
        } else if (logroId.equals("TAREAS_10") || logroId.startsWith("STREAK_7")) {
            return TipoMedalla.BRONCE;
        } else if (logroId.equals("TAREAS_20") || logroId.startsWith("STREAK_14")) {
            return TipoMedalla.PLATA;
        } else if (logroId.equals("TAREAS_30") || logroId.startsWith("STREAK_30")) {
            return TipoMedalla.ORO;
        } else {
            return TipoMedalla.NINGUNA;
        }
    }
}
