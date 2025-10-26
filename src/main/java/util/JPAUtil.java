package util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

/**
 * Utilidad para obtener EntityManager compartido.
 */
public class JPAUtil {

    private static final String PERSISTENCE_UNIT_NAME = "default";
    private static EntityManagerFactory emf;

    static {
        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (PersistenceException ex) {
            System.err.println("Error inicializando EntityManagerFactory: " + ex.getMessage());
            emf = null;
        }
    }

    public static EntityManager getEntityManager() {
        if (emf == null) {
            throw new IllegalStateException("EntityManagerFactory no inicializado. Verifique persistence.xml y dependencias.");
        }
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
