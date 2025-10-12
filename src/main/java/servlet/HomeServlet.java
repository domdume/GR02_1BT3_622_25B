package servlet;

import dao.MiembroHogarDAO;
import dao.QuehacerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MiembroHogar;
import model.Quehacer;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "HomeServlet", value = {"", "/home"})
public class HomeServlet extends HttpServlet {
    private QuehacerDAO quehacerDAO;
    private MiembroHogarDAO miembroHogarDAO;

    public void init() {
        quehacerDAO = new QuehacerDAO();
        miembroHogarDAO = new MiembroHogarDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Cargar datos para el tablero principal
        List<Quehacer> listaQuehaceres = quehacerDAO.findAllWithMiembroHogar();
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll(); // Ya usa LEFT JOIN FETCH
        
        System.out.println("[DEBUG] Cargando página principal:");
        System.out.println("[DEBUG] - Quehaceres: " + listaQuehaceres.size());
        System.out.println("[DEBUG] - Miembros: " + listaMiembros.size());
        
        // Calcular estadísticas del hogar
        long tareasCompletadas = listaQuehaceres.stream().filter(Quehacer::isEstadoCompletado).count();
        long tareasPendientes = listaQuehaceres.stream().filter(q -> !q.isEstadoCompletado() && !q.isEstadoFinalizado()).count();
        long tareasVencidas = listaQuehaceres.stream().filter(q -> q.isOverdue() && !q.isEstadoFinalizado()).count();
        
        // Encontrar el miembro con más puntos (MVP del hogar)
        MiembroHogar mvpMiembro = null;
        int maxPuntos = 0;
        for (MiembroHogar miembro : listaMiembros) {
            if (miembro.getPuntos() > maxPuntos) {
                maxPuntos = miembro.getPuntos();
                mvpMiembro = miembro;
            }
        }
        
        // Pasar datos al JSP
        request.setAttribute("listaQuehaceres", listaQuehaceres);
        request.setAttribute("listaMiembros", listaMiembros);
        request.setAttribute("tareasCompletadas", tareasCompletadas);
        request.setAttribute("tareasPendientes", tareasPendientes);
        request.setAttribute("tareasVencidas", tareasVencidas);
        request.setAttribute("mvpMiembro", mvpMiembro);
        
        request.getRequestDispatcher("/tablero.jsp").forward(request, response);
    }
}