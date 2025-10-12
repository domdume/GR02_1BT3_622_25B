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
        // Cargar lista de miembros existentes para mostrar en el formulario
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
        request.setAttribute("listaMiembros", listaMiembros);
        request.getRequestDispatcher("/miembros/form.jsp").forward(request, response);
    }

    private void insertMiembro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombre = request.getParameter("nombre");
        String edadStr = request.getParameter("edad");

        System.out.println("[DEBUG] Intentando agregar miembro:");
        System.out.println("[DEBUG] - Nombre: " + nombre);
        System.out.println("[DEBUG] - Edad: " + edadStr);

        try {
            // Validación de parámetros
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre es requerido");
            }
            
            if (edadStr == null || edadStr.trim().isEmpty()) {
                throw new IllegalArgumentException("La edad es requerida");
            }

            int edad = Integer.parseInt(edadStr);
            
            if (edad <= 0) {
                throw new IllegalArgumentException("La edad debe ser mayor a 0");
            }

            MiembroHogar nuevoMiembro = new MiembroHogar();
            nuevoMiembro.setNombre(nombre.trim());
            nuevoMiembro.setEdad(edad);
            miembroHogarDAO.create(nuevoMiembro);

            System.out.println("[DEBUG] Miembro creado exitosamente con ID: " + nuevoMiembro.getId());
            request.getSession().setAttribute("successMessage", "Miembro agregado correctamente: " + nombre);
            
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Error de formato en edad: " + e.getMessage());
            request.getSession().setAttribute("errorMessage", "La edad debe ser un número válido.");
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] Error de validación: " + e.getMessage());
            request.getSession().setAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            System.out.println("[ERROR] Error general al agregar miembro: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error al agregar el miembro: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/miembros?action=list");
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
