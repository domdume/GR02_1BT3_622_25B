package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Quehacer;
import util.JPAUtil;

import java.util.List;

public class QuehacerDAO {

    public void create(Quehacer quehacer) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
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
