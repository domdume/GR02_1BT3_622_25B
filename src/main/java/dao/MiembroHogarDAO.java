package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.MiembroHogar;
import util.JPAUtil;

import java.util.List;

public class MiembroHogarDAO {

    public void create(MiembroHogar miembro) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(miembro);
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

    public MiembroHogar findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(MiembroHogar.class, id);
        } finally {
            em.close();
        }
    }

    public List<MiembroHogar> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT m FROM MiembroHogar m", MiembroHogar.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void update(MiembroHogar miembro) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(miembro);
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
            MiembroHogar miembro = em.find(MiembroHogar.class, id);
            if (miembro != null) {
                em.remove(miembro);
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
