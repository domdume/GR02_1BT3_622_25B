package servlet;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import service.MiembroHogarService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MiembroHogar;
import service.HogarService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "MiembroServlet", value = "/miembros")

public class MiembroServlet extends HttpServlet {
    private MiembroHogarService miembroHogarService;
    private HogarService hogarService;

    @Override
    public void init() {
        System.out.println("[MiembroServlet] Inicializando servlet...");
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        this.miembroHogarService = ctx.getBean(MiembroHogarService.class);
        this.hogarService = ctx.getBean(HogarService.class);
        System.out.println("[MiembroServlet] Servicios obtenidos exitosamente");
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
        List<MiembroHogar> listaMiembros = miembroHogarService.obtenerTodos();
        if (listaMiembros == null || listaMiembros.isEmpty()) {
            request.getSession().setAttribute("errorMessage", "No hay miembros registrados.");
            request.setAttribute("totalMiembros", 0);
            request.setAttribute("jefeCount", 0);
            request.setAttribute("totalPuntos", 0);
            request.setAttribute("totalTareas", 0);
        } else {
            int totalMiembros = listaMiembros.size();
            int jefeCount = 0;
            int totalPuntos = 0;
            int totalTareas = 0;
            for (MiembroHogar miembro : listaMiembros) {
                if (miembro.getClass().getSimpleName().equals("JefeDelHogar")) {
                    jefeCount++;
                }
                totalPuntos += miembro.getPuntos();
                if (miembro.getQuehaceres() != null) {
                    totalTareas += miembro.getQuehaceres().size();
                }
            }
            request.setAttribute("totalMiembros", totalMiembros);
            request.setAttribute("jefeCount", jefeCount);
            request.setAttribute("totalPuntos", totalPuntos);
            request.setAttribute("totalTareas", totalTareas);
        }
        request.setAttribute("listaMiembros", listaMiembros);
        request.getRequestDispatcher("/miembros/index.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MiembroHogar> listaMiembros = miembroHogarService.obtenerTodos();
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

            //INTRODUCIR EXPLAINING VARIABLE: Determinar tipo de miembro
            String tipoMiembroParam = request.getParameter("tipoMiembro");
            boolean esSeleccionadoComoJefe = "jefe".equals(tipoMiembroParam);
            //Usar método de consulta directo
            boolean noExisteJefeActualmente = !hogarService.obtenerEstadisticasHogar().tieneJefe;
            boolean debeSerJefe = esSeleccionadoComoJefe || noExisteJefeActualmente;

            System.out.println("[MiembroServlet] Variables explicativas:");
            System.out.println("  - Tipo seleccionado: " + tipoMiembroParam);
            System.out.println("  - ¿Seleccionado como jefe?: " + esSeleccionadoComoJefe);
            System.out.println("  - ¿No existe jefe?: " + noExisteJefeActualmente);
            System.out.println("  - ¿Debe ser jefe?: " + debeSerJefe);

            hogarService.organizarMiembro(nombre.trim(), edad, debeSerJefe);
            System.out.println("[MiembroServlet] Miembro creado exitosamente a través de HogarService");

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
        miembroHogarService.eliminar(id);
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
        MiembroHogar miembro = miembroHogarService.obtenerPorId(id);
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
