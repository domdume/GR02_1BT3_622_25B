package servlet;

import dao.MiembroHogarDAO;
import service.EmblemaService;
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
    private MiembroHogarDAO miembroHogarDAO;
    private HogarService hogarService;

    public void init() {
        System.out.println("[MiembroServlet] Inicializando servlet...");
        try {
            miembroHogarDAO = new MiembroHogarDAO();
            System.out.println("[MiembroServlet] MiembroHogarDAO creado exitosamente");
            
            hogarService = new HogarService();
            System.out.println("[MiembroServlet] HogarService creado exitosamente");
            
        } catch (Exception e) {
            System.err.println("[MiembroServlet] Error durante inicialización: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al inicializar MiembroServlet", e);
        }
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
            // Estadísticas vacías
            request.setAttribute("totalMiembros", 0);
            request.setAttribute("jefeCount", 0);
            request.setAttribute("totalPuntos", 0);
            request.setAttribute("totalTareas", 0);
        } else {
            // Calcular estadísticas en el controlador (no en la vista)
            int totalMiembros = listaMiembros.size();
            int jefeCount = 0;
            int totalPuntos = 0;
            int totalTareas = 0;
            
            for (MiembroHogar miembro : listaMiembros) {
                // Contar jefes del hogar
                if (miembro.getClass().getSimpleName().equals("JefeDelHogar")) {
                    jefeCount++;
                }
                
                // Sumar puntos totales
                totalPuntos += miembro.getPuntos();
                
                // Contar tareas asignadas
                if (miembro.getQuehaceres() != null) {
                    totalTareas += miembro.getQuehaceres().size();
                }
            }
            
            // Pasar estadísticas calculadas a la vista
            request.setAttribute("totalMiembros", totalMiembros);
            request.setAttribute("jefeCount", jefeCount);
            request.setAttribute("totalPuntos", totalPuntos);
            request.setAttribute("totalTareas", totalTareas);
        }
        
        request.setAttribute("listaMiembros", listaMiembros); // Pasa los datos al JSP
        // Preparar emblemas (servicio en memoria). Esto permite mostrar badges en la vista.
        try {
            EmblemaService emblemaService;
            emblemaService = EmblemaService.getInstancia();
            java.util.Map<Long, java.util.Set<String>> emblemasPorMiembro = new java.util.HashMap<>();
            if (listaMiembros != null) {
                for (MiembroHogar miembro : listaMiembros) {
                    emblemasPorMiembro.put(miembro.getId(), emblemaService.obtenerEmblemas(miembro.getId()));
                }
            }
            request.setAttribute("emblemasPorMiembro", emblemasPorMiembro);
        } catch (Exception e) {
            // No bloquear la vista por errores del servicio de emblemas; simplemente no mostrarlos
            System.err.println("[MiembroServlet] Error al obtener emblemas: " + e.getMessage());
        }
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
                request.getSession().setAttribute("errorMessage", "El nombre es requerido.");
                response.sendRedirect(request.getContextPath() + "/miembros?action=new");
                return;
            }
            if (edadStr == null || edadStr.trim().isEmpty()) {
                request.getSession().setAttribute("errorMessage", "La edad es requerida.");
                response.sendRedirect(request.getContextPath() + "/miembros?action=new");
                return;
            }
            int edad;
            try {
                edad = Integer.parseInt(edadStr);
            } catch (Exception ex) {
                request.getSession().setAttribute("errorMessage", "La edad debe ser un número válido.");
                response.sendRedirect(request.getContextPath() + "/miembros?action=new");
                return;
            }
            if (edad <= 0) {
                request.getSession().setAttribute("errorMessage", "La edad debe ser mayor a 0.");
                response.sendRedirect(request.getContextPath() + "/miembros?action=new");
                return;
            }

            // Determinar tipo de miembro
            String tipoMiembroParam = request.getParameter("tipoMiembro");
            boolean esSeleccionadoComoJefe = "jefe".equals(tipoMiembroParam);
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
