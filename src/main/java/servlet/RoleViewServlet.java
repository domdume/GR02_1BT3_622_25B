package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "RoleViewServlet", value = "/viewas")
public class RoleViewServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String role = request.getParameter("role");
        if (role == null) role = "MIEMBRO";
        role = role.equalsIgnoreCase("JEFE") ? "JEFE" : "MIEMBRO";

        HttpSession session = request.getSession(true);
        session.setAttribute("viewRole", role);
        session.setAttribute("infoMessage", "Viendo la interfaz como: " + ("JEFE".equals(role) ? "Jefe del Hogar" : "Miembro del Hogar"));

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}

