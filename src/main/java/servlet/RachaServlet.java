package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ServicioRacha;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@WebServlet(name = "RachaServlet", value = "/rachas")
public class RachaServlet extends HttpServlet {
    private ServicioRacha servicioRacha;

    @Override
    public void init() {
        servicioRacha = new ServicioRacha();
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
                request.getSession().setAttribute("successMessage", "Se registró una tarea completada el " + dia + " para " + nombre.get());
            } else {
                request.getSession().setAttribute("errorMessage", "Miembro no encontrado");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID de miembro inválido");
        }
        response.sendRedirect(request.getContextPath() + "/rachas");
    }
}