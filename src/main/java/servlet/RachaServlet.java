package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.LogroService;
import service.ServicioRacha;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@WebServlet(name = "RachaServlet", value = "/rachas")
public class RachaServlet extends HttpServlet {
    private ServicioRacha servicioRacha;
    private LogroService logroService;

    @Override
    public void init() {
        servicioRacha = new ServicioRacha();
        logroService = new LogroService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = Optional.ofNullable(request.getParameter("action")).orElse("list");
        switch (action) {
            case "addToday":
                addQuickTask(request, response, LocalDate.now());
                return;
            case "addYesterday":
                addQuickTask(request, response, LocalDate.now().minusDays(1));
                return;
            case "addTwoDaysAgo":
                addQuickTask(request, response, LocalDate.now().minusDays(2));
                return;
            case "seedSix":
                seedSixDays(request, response);
                return;
            case "list":
            default:
                var data = servicioRacha.obtenerRachasActuales();
                request.setAttribute("miembros", data.miembrosOrdenados);
                request.setAttribute("rachaPorMiembro", data.rachaPorMiembro);
                request.getRequestDispatcher("/rachas/index.jsp").forward(request, response);
        }
    }

    private void addQuickTask(HttpServletRequest request, HttpServletResponse response, LocalDate dia) throws IOException {
        String miembroIdStr = request.getParameter("miembroId");
        if (miembroIdStr == null || miembroIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/rachas");
            return;
        }
        try {
            Long miembroId = Long.parseLong(miembroIdStr);
            var nombre = servicioRacha.registrarTareaRapida(miembroId, dia);
            if (nombre.isPresent()) {
                // Tras registrar la tarea, recalcular y otorgar el logro si aplica
                var data = servicioRacha.obtenerRachasActuales();
                int racha = data.rachaPorMiembro.getOrDefault(miembroId, 0);
                var notif = logroService.verificarYAsignarLogroRachaConTipo(miembroId, racha);
                String baseMsg = "Se registró una tarea completada el " + dia + " para " + nombre.get();
                if (notif.isPresent()) {
                    // Mensaje de éxito y datos para el toast
                    request.getSession().setAttribute("successMessage", baseMsg + ". " + notif.get().getMensaje());
                    request.getSession().setAttribute("achievementMessage", notif.get().getMensaje());
                    request.getSession().setAttribute("achievementLogroId", notif.get().getLogroId());
                } else {
                    request.getSession().setAttribute("successMessage", baseMsg);
                }
            } else {
                request.getSession().setAttribute("errorMessage", "Miembro no encontrado");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID de miembro inválido");
        }
        response.sendRedirect(request.getContextPath() + "/rachas");
    }

    // Crea una racha de 6 días consecutivos terminando ayer para el miembro
    private void seedSixDays(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String miembroIdStr = request.getParameter("miembroId");
        if (miembroIdStr == null || miembroIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/rachas");
            return;
        }
        try {
            Long miembroId = Long.parseLong(miembroIdStr);
            LocalDate hoy = LocalDate.now();
            // Crear tareas completadas para los últimos 6 días: ayer, -2, -3, -4, -5, -6
            for (int i = 1; i <= 6; i++) {
                servicioRacha.registrarTareaRapida(miembroId, hoy.minusDays(i));
            }
            request.getSession().setAttribute("successMessage", "Se simuló una racha de 6 días consecutivos (terminando ayer) para el miembro " + miembroId + ". Ahora use '+ Hoy' para alcanzar 7.");
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID de miembro inválido");
        }
        response.sendRedirect(request.getContextPath() + "/rachas");
    }
}