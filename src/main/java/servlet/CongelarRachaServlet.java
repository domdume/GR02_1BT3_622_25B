package servlet;

import dao.MiembroHogarDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MiembroHogar;

import java.io.IOException;

/**
 * Servlet para alternar el estado de "Proteger Racha" (congelamiento) de un miembro.
 * Solo muestra/permite la acción cuando la sesión se ve "como JEFE".
 */
@WebServlet(name = "CongelarRachaServlet", value = "/miembros/congelar")
public class CongelarRachaServlet extends HttpServlet {
    private MiembroHogarDAO miembroDAO;

    @Override
    public void init() {
        miembroDAO = new MiembroHogarDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("miembroId");
        String estadoStr = request.getParameter("freeze"); // "true" o "false"

        if (idStr == null) {
            request.getSession().setAttribute("errorMessage", "Miembro no especificado");
            response.sendRedirect(request.getContextPath() + "/rachas");
            return;
        }

        try {
            Long miembroId = Long.parseLong(idStr);
            MiembroHogar miembro = miembroDAO.findById(miembroId);
            if (miembro == null) {
                request.getSession().setAttribute("errorMessage", "Miembro no encontrado");
                response.sendRedirect(request.getContextPath() + "/rachas");
                return;
            }

            boolean freeze = "true".equalsIgnoreCase(estadoStr) || "on".equalsIgnoreCase(estadoStr);
            miembro.setRachaCongelada(freeze);
            miembroDAO.update(miembro);

            request.getSession().setAttribute("successMessage", (freeze ? "Racha protegida (❄️) para " : "Racha desprotegida para ") + miembro.getNombre());
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID de miembro inválido");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "No se pudo actualizar el estado de racha: " + e.getMessage());
        }

        // Volver a la lista de rachas (u origen si viene de otra página)
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect(request.getContextPath() + "/rachas");
        }
    }
}

