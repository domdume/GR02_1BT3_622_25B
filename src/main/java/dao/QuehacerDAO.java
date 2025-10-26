package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Quehacer;
import model.MiembroHogar;
import util.JPAUtil;

import java.util.List;
import java.util.logging.Logger;

public class QuehacerDAO {

    private static final Logger logger = Logger.getLogger(QuehacerDAO.class.getName());

    public void create(Quehacer quehacer) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
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
                logger.info("Quehacer persistido correctamente: " + quehacer.getNombre());
            } catch (Exception e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                logger.severe("Error al persistir quehacer: " + e.getMessage());
            }
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
        try (EntityManager em = JPAUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                // Siempre usar merge y obtener la instancia gestionada para evitar problemas
                // con entidades detached. Merge devuelve la instancia gestionada.
                Quehacer managed = em.merge(quehacer);
                // Forzar sincronización inmediata para detectar errores ahora
                em.flush();
                if (managed != null) {
                    logger.info("Quehacer actualizado en EM (id=" + managed.getId() + ", estado=" + managed.getEstado() + ")");
                }
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                logger.severe("Error al actualizar quehacer: " + e.getMessage());
            }
        }
    }

    /**
     * Marca un quehacer como VENCIDO directamente en la base de datos usando JPQL update.
     * Esto evita problemas con entidades detached o colecciones que puedan sobrescribir cambios.
     */
    public void markAsVencido(Long quehacerId, java.time.LocalDateTime fechaFinalizacion) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                int updated = em.createQuery("UPDATE Quehacer q SET q.estado = :estado, q.fechaFinalizacion = :fecha WHERE q.id = :id")
                        .setParameter("estado", model.EstadoQuehacer.VENCIDO)
                        .setParameter("fecha", fechaFinalizacion)
                        .setParameter("id", quehacerId)
                        .executeUpdate();
                em.flush();
                logger.info("markAsVencido - filas actualizadas: " + updated + " para quehacerId=" + quehacerId);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                logger.severe("Error en markAsVencido: " + e.getMessage());
            }
        }
    }

    public void delete(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
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
                logger.severe("Error al eliminar quehacer: " + e.getMessage());
            }
        }
    }
}
