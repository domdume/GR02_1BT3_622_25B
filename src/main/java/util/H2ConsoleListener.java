package util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.h2.tools.Server;
import java.sql.SQLException;

@WebListener
public class H2ConsoleListener implements ServletContextListener {

    private Server webServer;
    private Server tcpServer;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Iniciar el servidor web de la consola H2 en el puerto 8082
            this.webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
            this.webServer.start();
            System.out.println("H2 Console started on port 8082");

            // Iniciar el servidor TCP para permitir conexiones a la base de datos en memoria
            this.tcpServer = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
            this.tcpServer.start();
            System.out.println("H2 TCP server started on port 9092");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to start H2 server", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (this.webServer != null) {
            this.webServer.stop();
        }
        if (this.tcpServer != null) {
            this.tcpServer.stop();
        }
    }
}
