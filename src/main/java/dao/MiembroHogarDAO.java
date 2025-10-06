package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.MiembroHogar;
import util.JPAUtil;

import java.util.ArrayList;
import java.util.List;

public class MiembroHogarDAO {

    public void create(MiembroHogar miembro) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = null;
        try {
            System.out.println("[DAO] Iniciando creación de miembro: " + miembro.getNombre());
            tx = em.getTransaction();
            tx.begin();
            
            // Verificar que el miembro tenga los datos necesarios
            if (miembro.getNombre() == null || miembro.getNombre().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del miembro no puede estar vacío");
            }
            
            if (miembro.getEdad() <= 0) {
                throw new IllegalArgumentException("La edad debe ser mayor a 0");
            }
            
            em.persist(miembro);
            tx.commit();
            System.out.println("[DAO] Miembro persistido exitosamente con ID: " + miembro.getId());
            
        } catch (Exception e) {
            System.out.println("[DAO ERROR] Error al crear miembro: " + e.getMessage());
            e.printStackTrace();
            if (tx != null && tx.isActive()) {
                tx.rollback();
                System.out.println("[DAO] Transaction rollback realizado");
            }
            throw new RuntimeException("Error al persistir el miembro: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public MiembroHogar findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT m FROM MiembroHogar m LEFT JOIN FETCH m.quehaceres WHERE m.id = :id", MiembroHogar.class)
                     .setParameter("id", id)
                     .getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<MiembroHogar> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<MiembroHogar> miembros = em.createQuery("SELECT m FROM MiembroHogar m", MiembroHogar.class).getResultList();
            System.out.println("Miembros recuperados: " + miembros);
            return miembros;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al recuperar miembros: " + e.getMessage());
            return new ArrayList<>(); // Devuelve una lista vacía en caso de error
        } finally {
            em.close();
        }
    }

    public void update(MiembroHogar miembro) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (miembro != null) {
                em.merge(miembro);
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

    public List<MiembroHogar> obtenerTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            jakarta.persistence.TypedQuery<MiembroHogar> query = em.createQuery("SELECT m FROM MiembroHogar m", MiembroHogar.class);
            List<MiembroHogar> miembros = query.getResultList();
            System.out.println("Miembros obtenidos con obtenerTodos(): " + miembros);
            return miembros;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error en obtenerTodos(): " + e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
}
