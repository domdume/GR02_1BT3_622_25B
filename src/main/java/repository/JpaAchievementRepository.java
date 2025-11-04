package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import model.Logro;
import model.MiembroHogar;
import util.JPAUtil;

public class JpaAchievementRepository implements AchievementRepository {

    @Override
    public boolean tieneLogro(Long miembroId, String logroId) {
        if (miembroId == null || logroId == null) return false;
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(a) FROM Logro a WHERE a.miembro.id = :mid AND a.logroId = :lid",
                            Long.class)
                    .setParameter("mid", miembroId)
                    .setParameter("lid", logroId)
                    .getSingleResult();
            return count != null && count > 0;
        } catch (Exception ex) {
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public void guardarLogro(Long miembroId, String logroId) {
        if (miembroId == null || logroId == null) return;
        EntityManager em = JPAUtil.getEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            MiembroHogar miembro = em.find(MiembroHogar.class, miembroId);
            if (miembro == null) throw new NoResultException("Miembro no encontrado: " + miembroId);
            Logro a = new Logro(miembro, logroId);
            em.persist(a);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}