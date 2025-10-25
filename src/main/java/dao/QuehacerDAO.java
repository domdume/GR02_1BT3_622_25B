package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Quehacer;
import model.MiembroHogar;
import util.JPAUtil;

import java.util.List;

public class QuehacerDAO {

    public void create(Quehacer quehacer) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Ensure the miembroHogar reference is attached to this EM
            if (quehacer.getMiembroHogar() != null && quehacer.getMiembroHogar().getId() != null) {
                MiembroHogar ref = em.getReference(MiembroHogar.class, quehacer.getMiembroHogar().getId());
                quehacer.setMiembroHogar(ref);
            }
            em.persist(quehacer);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public Quehacer findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Quehacer.class, id);
        } finally {
            em.close();
        }
    }

    public List<Quehacer> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT q FROM Quehacer q", Quehacer.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Quehacer> findAllWithMiembroHogar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT q FROM Quehacer q LEFT JOIN FETCH q.miembroHogar", Quehacer.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<model.MiembroHogar> findAllMiembrosWithQuehaceres() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT DISTINCT m FROM MiembroHogar m LEFT JOIN FETCH m.quehaceres", model.MiembroHogar.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void update(Quehacer quehacer) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(quehacer);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Quehacer quehacer = em.find(Quehacer.class, id);
            if (quehacer != null) {
                em.remove(quehacer);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
