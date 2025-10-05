package data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import util.JPAUtil;

import java.util.List;

/**
 * Clase Persistencia según diagrama UML - Package Data
 * Implementa los métodos básicos de persistencia usando JPA
 */
public class Persistencia {
    
    /**
     * Método para guardar una entidad
     * Según diagrama UML
     */
    public static <T> void guardar(T entidad) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            em.persist(entidad);
            transaction.commit();
            System.out.println("Entidad guardada: " + entidad.getClass().getSimpleName());
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error al guardar entidad: " + e.getMessage());
            throw new RuntimeException("Error en operación de guardado", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Método para buscar una entidad por ID
     * Según diagrama UML
     */
    public static <T> T buscar(Class<T> claseEntidad, Object id) {
        EntityManager em = JPAUtil.getEntityManager();
        
        try {
            T entidad = em.find(claseEntidad, id);
            System.out.println("Entidad buscada: " + claseEntidad.getSimpleName() + " con ID: " + id);
            return entidad;
        } catch (Exception e) {
            System.err.println("Error al buscar entidad: " + e.getMessage());
            throw new RuntimeException("Error en operación de búsqueda", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Método para buscar todas las entidades de un tipo
     */
    public static <T> List<T> buscarTodos(Class<T> claseEntidad) {
        EntityManager em = JPAUtil.getEntityManager();
        
        try {
            String nombreEntidad = claseEntidad.getSimpleName();
            TypedQuery<T> query = em.createQuery("SELECT e FROM " + nombreEntidad + " e", claseEntidad);
            List<T> resultados = query.getResultList();
            System.out.println("Encontradas " + resultados.size() + " entidades de tipo: " + nombreEntidad);
            return resultados;
        } catch (Exception e) {
            System.err.println("Error al buscar todas las entidades: " + e.getMessage());
            throw new RuntimeException("Error en operación de búsqueda múltiple", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Método para actualizar una entidad
     * Según diagrama UML
     */
    public static <T> void actualizar(T entidad) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            em.merge(entidad);
            transaction.commit();
            System.out.println("Entidad actualizada: " + entidad.getClass().getSimpleName());
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error al actualizar entidad: " + e.getMessage());
            throw new RuntimeException("Error en operación de actualización", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Método para eliminar una entidad
     * Según diagrama UML
     */
    public static <T> void eliminar(Class<T> claseEntidad, Object id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        
        try {
            transaction.begin();
            T entidad = em.find(claseEntidad, id);
            if (entidad != null) {
                em.remove(entidad);
                System.out.println("Entidad eliminada: " + claseEntidad.getSimpleName() + " con ID: " + id);
            } else {
                System.out.println("No se encontró la entidad para eliminar: " + claseEntidad.getSimpleName() + " con ID: " + id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error al eliminar entidad: " + e.getMessage());
            throw new RuntimeException("Error en operación de eliminación", e);
        } finally {
            em.close();
        }
    }
}