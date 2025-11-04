package repository;

// import model.TipoLogro; // unused
import model.MiembroHogar;
import model.Logro;
import util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class JpaAchievementRepository implements AchievementRepository {

    @Override
    public boolean tieneLogro(Long miembroId, String logroId) {
        if (miembroId == null || logroId == null) return false;

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                            "SELECT COUNT(l) FROM Logro l WHERE l.miembro.id = :miembroId AND l.logroId = :logroId",
                            Long.class)
                    .setParameter("miembroId", miembroId)
                    .setParameter("logroId", logroId);

            return query.getSingleResult() > 0;
        } catch (Exception ex) {
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void guardarLogro(Long miembroId, String logroId) {
        if (miembroId == null || logroId == null) return;
        if (tieneLogro(miembroId, logroId)) return;

        EntityManager em = JPAUtil.getEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();

            MiembroHogar miembro = em.find(MiembroHogar.class, miembroId);
            if (miembro == null) {
                throw new NoResultException("Miembro no encontrado con ID: " + miembroId);
            }

            Logro logro = new Logro(miembro, logroId);
            //logro.setTipoLogro(obtenerTipoLogro(logroId));
            //logro.setNivel(obtenerNivel(logroId));

            em.persist(logro);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al guardar el logro: " + ex.getMessage(), ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public int obtenerTareasCompletadas(Long miembroId) {
        return 0;
    }


    @Override
    public void incrementarContadorTareas(Long miembroId) {
        if (miembroId == null) return;

        EntityManager em = JPAUtil.getEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();

            MiembroHogar miembro = em.find(MiembroHogar.class, miembroId);
            if (miembro == null) {
                tx.rollback();
                return; // Si el miembro no existe, simplemente retornamos sin lanzar excepción
            }

            //miembro.setTareasCompletadas(miembro.getTareasCompletadas() + 1);
            em.merge(miembro);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al incrementar contador: " + ex.getMessage(), ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public boolean tieneCualquierLogro(Long miembroId) {
        if (miembroId == null) return false;
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                            "SELECT COUNT(l) FROM Logro l WHERE l.miembro.id = :miembroId",
                            Long.class)
                    .setParameter("miembroId", miembroId);
            return query.getSingleResult() > 0;
        } catch (Exception ex) {
            return false;
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    /*private TipoLogro obtenerTipoLogro(String logroId) {
        if (logroId == null) return TipoLogro.TROFEO;

        if (logroId.startsWith("TAREAS_")) {
            return TipoLogro.MEDALLA;
        } else if (logroId.startsWith("STREAK_")) {
            return TipoLogro.EMBLEMA;
        } else if (logroId.startsWith("POINTS_")) {
            return TipoLogro.INSIGNIA;
        }
        return TipoLogro.TROFEO;
    }*/
}