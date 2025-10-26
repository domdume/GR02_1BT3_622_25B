package servlet;

import dao.IncentivoDAO;
import dao.MiembroHogarDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Incentivo;
import model.MiembroHogar;
import model.TipoIncentivo;
import util.RouteController;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "IncentivoServlet", value = "/incentivos")
public class IncentivoServlet extends HttpServlet {
    private IncentivoDAO incentivoDAO;
    private MiembroHogarDAO miembroHogarDAO;

    @Override
    public void init() {
        incentivoDAO = new IncentivoDAO();
        miembroHogarDAO = new MiembroHogarDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = RouteController.getDefaultAction();
        }

        switch (action) {
            case "new":
                showNewForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "history":
                showHistory(request, response);
                break;
            case "statistics":
                showStatistics(request, response);
                break;
            case "byMiembro":
                showIncentivosByMiembro(request, response);
                break;
            case "toggle":
                toggleIncentivo(request, response);
                break;
            default:
                listAllIncentivos(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        switch (action) {
            case "create":
                createIncentivo(request, response);
                break;
            case "update":
                updateIncentivo(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/" + RouteController.MODULE_INCENTIVOS);
                break;
        }
    }

    private void listAllIncentivos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Incentivo> listaIncentivos = incentivoDAO.findAll();
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
        
        request.setAttribute("listaIncentivos", listaIncentivos);
        request.setAttribute("listaMiembros", listaMiembros);
        request.getRequestDispatcher(RouteController.getView(RouteController.MODULE_INCENTIVOS, RouteController.ACTION_LIST)).forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
        request.setAttribute("listaMiembros", listaMiembros);
        request.getRequestDispatcher(RouteController.getView(RouteController.MODULE_INCENTIVOS, RouteController.ACTION_NEW)).forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null) {
            request.getSession().setAttribute("errorMessage", "ID de incentivo no especificado.");
            response.sendRedirect(RouteController.buildListURL(request.getContextPath(), RouteController.MODULE_INCENTIVOS));
            return;
        }

        try {
            Long id = Long.parseLong(idStr);
            Incentivo incentivo = incentivoDAO.findById(id);
            
            if (incentivo == null) {
                request.getSession().setAttribute("errorMessage", "Incentivo no encontrado.");
                response.sendRedirect(RouteController.buildListURL(request.getContextPath(), RouteController.MODULE_INCENTIVOS));
                return;
            }

            List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
            request.setAttribute("incentivo", incentivo);
            request.setAttribute("listaMiembros", listaMiembros);
            request.getRequestDispatcher(RouteController.getView(RouteController.MODULE_INCENTIVOS, RouteController.ACTION_EDIT)).forward(request, response);
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID de incentivo inválido.");
            response.sendRedirect(RouteController.buildListURL(request.getContextPath(), RouteController.MODULE_INCENTIVOS));
        }
    }

    private void showHistory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Incentivo> listaIncentivos = incentivoDAO.findAll(); // Usar método disponible
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
        
        request.setAttribute("listaIncentivos", listaIncentivos);
        request.setAttribute("listaMiembros", listaMiembros);
        request.getRequestDispatcher("/incentivos/history.jsp").forward(request, response);
    }

    private void showStatistics(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Incentivo> listaIncentivos = incentivoDAO.findAll();
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
        
        // Calcular estadísticas (esto se podría mover a un service)
        request.setAttribute("listaIncentivos", listaIncentivos);
        request.setAttribute("listaMiembros", listaMiembros);
        request.getRequestDispatcher("/incentivos/statistics.jsp").forward(request, response);
    }

    private void toggleIncentivo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Por ahora, simplemente redirect ya que el modelo actual no tiene campo 'disponible'
        request.getSession().setAttribute("errorMessage", "Funcionalidad de toggle aún no implementada en el modelo actual.");
        response.sendRedirect(RouteController.buildListURL(request.getContextPath(), RouteController.MODULE_INCENTIVOS));
    }

    private void createIncentivo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String descripcion = request.getParameter("descripcion");
            String tipoIncentivoStr = request.getParameter("tipoIncentivo");
            String puntosStr = request.getParameter("puntos");

            // Validaciones básicas
            if (descripcion == null || descripcion.trim().isEmpty()) {
                request.getSession().setAttribute("errorMessage", "La descripción del incentivo es obligatoria.");
                response.sendRedirect(RouteController.buildNewURL(request.getContextPath(), RouteController.MODULE_INCENTIVOS));
                return;
            }

            if (tipoIncentivoStr == null || tipoIncentivoStr.trim().isEmpty()) {
                request.getSession().setAttribute("errorMessage", "El tipo de incentivo es obligatorio.");
                response.sendRedirect(RouteController.buildNewURL(request.getContextPath(), RouteController.MODULE_INCENTIVOS));
                return;
            }

            TipoIncentivo tipoIncentivo;
            try {
                tipoIncentivo = TipoIncentivo.valueOf(tipoIncentivoStr);
            } catch (Exception ex) {
                request.getSession().setAttribute("errorMessage", "El tipo de incentivo es inválido.");
                response.sendRedirect(RouteController.buildNewURL(request.getContextPath(), RouteController.MODULE_INCENTIVOS));
                return;
            }

            int puntos = 0;
            if (puntosStr != null && !puntosStr.trim().isEmpty()) {
                try {
                    puntos = Integer.parseInt(puntosStr);
                } catch (Exception ex) {
                    request.getSession().setAttribute("errorMessage", "Los puntos deben ser un número válido.");
                    response.sendRedirect(RouteController.buildNewURL(request.getContextPath(), RouteController.MODULE_INCENTIVOS));
                    return;
                }
            }

            // Crear incentivo básico
            Incentivo incentivo = new Incentivo();
            incentivo.setDescripcion(descripcion.trim());
            incentivo.setTipoIncentivo(tipoIncentivo);
            incentivo.setPuntos(puntos);

            incentivoDAO.create(incentivo);
            request.getSession().setAttribute("successMessage", "Incentivo creado exitosamente.");
            
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error al crear incentivo: " + e.getMessage());
        }

        response.sendRedirect(RouteController.buildListURL(request.getContextPath(), RouteController.MODULE_INCENTIVOS));
    }

    private void updateIncentivo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idStr = request.getParameter("id");
            if (idStr == null) {
                request.getSession().setAttribute("errorMessage", "ID de incentivo no especificado.");
                response.sendRedirect(RouteController.buildListURL(request.getContextPath(), RouteController.MODULE_INCENTIVOS));
                return;
            }

            Long id = Long.parseLong(idStr);
            Incentivo incentivo = incentivoDAO.findById(id);
            
            if (incentivo == null) {
                request.getSession().setAttribute("errorMessage", "Incentivo no encontrado.");
                response.sendRedirect(RouteController.buildListURL(request.getContextPath(), RouteController.MODULE_INCENTIVOS));
                return;
            }

            // Actualizar campos disponibles en el modelo actual
            String descripcion = request.getParameter("descripcion");
            String tipoIncentivoStr = request.getParameter("tipoIncentivo");
            String puntosStr = request.getParameter("puntos");

            if (descripcion != null && !descripcion.trim().isEmpty()) {
                incentivo.setDescripcion(descripcion.trim());
            }
            
            if (tipoIncentivoStr != null && !tipoIncentivoStr.trim().isEmpty()) {
                incentivo.setTipoIncentivo(TipoIncentivo.valueOf(tipoIncentivoStr));
            }
            
            if (puntosStr != null && !puntosStr.trim().isEmpty()) {
                incentivo.setPuntos(Integer.parseInt(puntosStr));
            }

            incentivoDAO.update(incentivo);
            request.getSession().setAttribute("successMessage", "Incentivo actualizado exitosamente.");
            
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error al actualizar incentivo: " + e.getMessage());
        }

        response.sendRedirect(RouteController.buildListURL(request.getContextPath(), RouteController.MODULE_INCENTIVOS));
    }

    private void showIncentivosByMiembro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String miembroIdStr = request.getParameter("miembroId");
        if (miembroIdStr != null) {
            Long miembroId = Long.parseLong(miembroIdStr);
            List<Incentivo> incentivos = incentivoDAO.findByMiembro(miembroId);
            MiembroHogar miembro = miembroHogarDAO.findById(miembroId);

            request.setAttribute("incentivos", incentivos);
            request.setAttribute("miembro", miembro);
        }
        request.getRequestDispatcher("/incentivos/byMiembro.jsp").forward(request, response);
    }
}
