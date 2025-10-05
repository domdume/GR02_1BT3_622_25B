package servlet;

import dao.MiembroHogarDAO;
import gui.InterfazDelJefe;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.JefeDelHogar;
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
            case "tasks":
                showTasks(request, response);
                break;
            default:
                listMiembros(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("insert".equals(action)) {
            insertMiembro(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/miembros/index.jsp");
        }
    }

    private void listMiembros(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll(); // Recupera los datos de la BD
        if (listaMiembros == null || listaMiembros.isEmpty()) {
            request.getSession().setAttribute("errorMessage", "No hay miembros registrados.");
        }
        request.setAttribute("listaMiembros", listaMiembros); // Pasa los datos al JSP
        request.getRequestDispatcher("/miembros/index.jsp").forward(request, response); // Redirige al JSP
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Ejecutar funcionalidad de InterfazDelJefe según diagrama UML
        System.out.println("=== EJECUTANDO LÓGICA DEL DIAGRAMA UML: InterfazDelJefe.registrarUnMiembro() ===");
        JefeDelHogar jefe = new JefeDelHogar("Jefe del Hogar", 45);
        new InterfazDelJefe(jefe); // Ejecutar lógica UML
        // La lógica se ejecuta en el constructor y métodos de la interfaz
        
        // Cargar lista de miembros existentes para mostrar en el formulario
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
        request.setAttribute("listaMiembros", listaMiembros);
        request.setAttribute("mensaje", "🏗️ Lógica del Diagrama UML ejecutada: InterfazDelJefe.registrarUnMiembro()");
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
        String edadStr = request.getParameter("edad");

        try {
            int edad = Integer.parseInt(edadStr);

            MiembroHogar nuevoMiembro = new MiembroHogar();
            nuevoMiembro.setNombre(nombre);
            nuevoMiembro.setEdad(edad);

            miembroHogarDAO.create(nuevoMiembro);

            request.getSession().setAttribute("successMessage", "Miembro agregado correctamente.");
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "La edad debe ser un número válido.");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error al agregar el miembro.");
        }

        response.sendRedirect(request.getContextPath() + "/miembros?action=list");
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

    // Nuevo método: mostrar tareas de un miembro
    private void showTasks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null) {
            request.getSession().setAttribute("errorMessage", "Miembro no especificado.");
            response.sendRedirect(request.getContextPath() + "/miembros");
            return;
        }
        Long id = Long.parseLong(idStr);
        MiembroHogar miembro = miembroHogarDAO.findById(id);
        if (miembro == null) {
            request.getSession().setAttribute("errorMessage", "Miembro no encontrado.");
            response.sendRedirect(request.getContextPath() + "/miembros");
            return;
        }
        request.setAttribute("miembro", miembro);
        request.setAttribute("listaQuehaceres", miembro.getQuehaceres());
        request.getRequestDispatcher("/miembros/tasks.jsp").forward(request, response);
    }
}
