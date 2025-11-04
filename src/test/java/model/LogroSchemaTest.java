package model;

import org.junit.Test;
import static org.junit.Assert.*;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class LogroSchemaTest {

    @Test
    public void creaTablaLogro_y_enumTieneTresValores() throws Exception {
        // Verificar enum TipoLogro
        TipoLogro[] vals = TipoLogro.values();
        assertEquals("TipoLogro debe tener 3 valores", 3, vals.length);
        boolean hasMedalla = false, hasRacha = false, hasEmblema = false;
        for (TipoLogro t : vals) {
            if (t.name().equals("MEDALLA")) hasMedalla = true;
            if (t.name().equals("LOGRO_RACHA")) hasRacha = true;
            if (t.name().equals("EMBLEMA")) hasEmblema = true;
        }
        assertTrue(hasMedalla);
        assertTrue(hasRacha);
        assertTrue(hasEmblema);

        // Crear EntityManagerFactory con H2 en memoria y forzar creación de esquema
        java.util.Map<String, Object> props = new java.util.HashMap<>();
        String url = "jdbc:h2:mem:logrotest;DB_CLOSE_DELAY=-1";
        props.put("jakarta.persistence.jdbc.url", url);
        props.put("jakarta.persistence.jdbc.user", "sa");
        props.put("jakarta.persistence.jdbc.password", "");
        props.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        props.put("hibernate.hbm2ddl.auto", "create");
        props.put("hibernate.show_sql", "false");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default", props);
        try {
            // Conectar por JDBC al mismo in-memory y comprobar la existencia de la tabla
            try (Connection conn = DriverManager.getConnection(url, "sa", "")) {
                DatabaseMetaData md = conn.getMetaData();
                ResultSet rs = md.getTables(null, null, "LOGRO", new String[]{"TABLE"});
                boolean found = rs.next();
                rs.close();
                assertTrue("Tabla LOGRO debe existir en la BD", found);

                // Verificar columnas esperadas
                ResultSet cols = md.getColumns(null, null, "LOGRO", null);
                java.util.Set<String> colSet = new java.util.HashSet<>();
                while (cols.next()) {
                    colSet.add(cols.getString("COLUMN_NAME").toUpperCase());
                }
                cols.close();
                String[] expected = {"ID","MIEMBRO_ID","LOGRO_ID","FECHA_CREACION","TIPO_LOGRO","NIVEL_MEDALLA","TAREAS_REQUERIDAS"};
                for (String c: expected) {
                    assertTrue("Columna esperada " + c + " en LOGRO", colSet.contains(c));
                }
            }
        } finally {
            emf.close();
        }
    }
}
