package servlet;

import dao.MiembroHogarDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MiembroHogar;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "MiembroServlet", value = "/miembros")
public class MiembroServlet extends HttpServlet {
    private MiembroHogarDAO miembroHogarDAO;

    public void init() {
        miembroHogarDAO = new MiembroHogarDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "new":
                showNewForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteMiembro(request, response);
                break;
            default:
                listMiembros(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "insert":
                insertMiembro(request, response);
                break;
            case "update":
                updateMiembro(request, response);
                break;
            default:
                listMiembros(request, response);
                break;
        }
    }

    private void listMiembros(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
        request.setAttribute("listaMiembros", listaMiembros);
        request.getRequestDispatcher("/miembros/index.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/miembros/form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        MiembroHogar miembroExistente = miembroHogarDAO.findById(id);
        request.setAttribute("miembro", miembroExistente);
        request.getRequestDispatcher("/miembros/form.jsp").forward(request, response);
    }

    private void insertMiembro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombre = request.getParameter("nombre");
        int edad = Integer.parseInt(request.getParameter("edad"));
        MiembroHogar nuevoMiembro = new MiembroHogar(nombre, edad);
        miembroHogarDAO.create(nuevoMiembro);
        response.sendRedirect(request.getContextPath() + "/miembros");
    }

    private void updateMiembro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        String nombre = request.getParameter("nombre");
        int edad = Integer.parseInt(request.getParameter("edad"));
        MiembroHogar miembro = miembroHogarDAO.findById(id);
        miembro.setNombre(nombre);
        miembro.setEdad(edad);
        miembroHogarDAO.update(miembro);
        response.sendRedirect(request.getContextPath() + "/miembros");
    }

    private void deleteMiembro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        miembroHogarDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/miembros");
    }
}
