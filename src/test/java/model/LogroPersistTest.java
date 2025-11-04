package model;

import org.junit.Test;
import static org.junit.Assert.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class LogroPersistTest {

    @Test
    public void persisteMiembroYLogro_conFK() throws Exception {
        Map<String, Object> props = new HashMap<>();
        String url = "jdbc:h2:mem:logropersist;DB_CLOSE_DELAY=-1";
        props.put("jakarta.persistence.jdbc.url", url);
        props.put("jakarta.persistence.jdbc.user", "sa");
        props.put("jakarta.persistence.jdbc.password", "");
        props.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        props.put("hibernate.hbm2ddl.auto", "create");
        props.put("hibernate.show_sql", "false");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default", props);
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            MiembroHogar m = new MiembroHogar("Ana", 28);
            em.persist(m);
            em.flush(); // ensure id assigned
            Long miembroId = m.getId();

            Logro l = new Logro(m, "EMBLEMA_ASCENSO");
            l.setTipoLogro(TipoLogro.EMBLEMA);
            l.setTareasRequeridas(0);
            em.persist(l);

            em.getTransaction().commit();

            // verify via JPA
            Logro found = em.find(Logro.class, l.getId());
            assertNotNull("Logro persisted", found);
            assertNotNull("Miembro asociado no debe ser nulo", found.getMiembro());
            assertEquals("FK debe referenciar al miembro persistido", miembroId, found.getMiembro().getId());

        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
            emf.close();
        }
    }
}
