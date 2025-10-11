package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Incentivo;
import util.JPAUtil;

import java.util.ArrayList;
import java.util.List;

public class IncentivoDAO {

    public void create(Incentivo incentivo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = null;
        try {
            System.out.println("[INCENTIVO-DAO] Creando incentivo: " + incentivo.getTipoIncentivo());
            tx = em.getTransaction();
            tx.begin();
            em.persist(incentivo);
            tx.commit();
            System.out.println("[INCENTIVO-DAO] Incentivo creado exitosamente con ID: " + incentivo.getId());
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.out.println("[ERROR] Error al crear incentivo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<Incentivo> findByMiembro(Long miembroId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Incentivo> incentivos = em.createQuery(
                            "SELECT i FROM Incentivo i WHERE i.miembroHogar.id = :miembroId",
                            Incentivo.class)
                    .setParameter("miembroId", miembroId)
                    .getResultList();
            System.out.println("[INCENTIVO-DAO] Encontrados " + incentivos.size() + " incentivos para miembro ID: " + miembroId);
            return incentivos;
        } catch (Exception e) {
            System.out.println("[ERROR] Error al buscar incentivos por miembro: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public List<Incentivo> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Incentivo> incentivos = em.createQuery("SELECT i FROM Incentivo i", Incentivo.class).getResultList();
            System.out.println("[INCENTIVO-DAO] Total de incentivos en BD: " + incentivos.size());
            return incentivos;
        } catch (Exception e) {
            System.out.println("[ERROR] Error al obtener todos los incentivos: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public Incentivo findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Incentivo.class, id);
        } catch (Exception e) {
            System.out.println("[ERROR] Error al buscar incentivo por ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public void update(Incentivo incentivo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.merge(incentivo);
            tx.commit();
            System.out.println("[INCENTIVO-DAO] Incentivo actualizado: " + incentivo.getId());
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.out.println("[ERROR] Error al actualizar incentivo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            Incentivo incentivo = em.find(Incentivo.class, id);
            if (incentivo != null) {
                em.remove(incentivo);
                System.out.println("[INCENTIVO-DAO] Incentivo eliminado: " + id);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.out.println("[ERROR] Error al eliminar incentivo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}