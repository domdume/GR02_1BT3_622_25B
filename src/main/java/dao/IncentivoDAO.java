package dao;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.Incentivo;
import util.JPAUtil;

public class IncentivoDAO {
    public List<Incentivo> findByMiembro(Long miembroId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Incentivo> q = em.createQuery(
                "SELECT i FROM Incentivo i WHERE i.miembroHogar.id = :miembroId", Incentivo.class);
            q.setParameter("miembroId", miembroId);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Incentivo> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Incentivo> q = em.createQuery("SELECT i FROM Incentivo i", Incentivo.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Incentivo findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Incentivo.class, id);
        } finally {
            em.close();
        }
    }

    public void create(Incentivo i) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(i);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    /**
     * Crea un Incentivo y lo asocia por referencia al miembro y al quehacer usando getReference
     * para evitar problemas con entidades detached en el caller.
     */
    public void createWithReferences(Long miembroId, Long quehacerId, Incentivo incentivo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (miembroId != null) {
                model.MiembroHogar ref = em.getReference(model.MiembroHogar.class, miembroId);
                incentivo.setMiembroHogar(ref);
            }
            if (quehacerId != null) {
                model.Quehacer refQ = em.getReference(model.Quehacer.class, quehacerId);
                incentivo.setQuehacer(refQ);
            }
            em.persist(incentivo);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public Incentivo update(Incentivo i) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Incentivo merged = em.merge(i);
            em.getTransaction().commit();
            return merged;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Incentivo i = em.find(Incentivo.class, id);
            if (i != null) em.remove(i);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
