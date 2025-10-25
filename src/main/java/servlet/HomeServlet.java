package servlet;

import dao.MiembroHogarDAO;
import dao.QuehacerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Liga;
import model.MiembroHogar;
import model.Quehacer;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
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

        // Formateo de fechas para vista
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Quehacer q : listaQuehaceres) {
            if (q.getTiempoLimite() != null) {
                q.setTiempoLimiteFmt(q.getTiempoLimite().format(fmt));
            }
            if (q.getFechaFinalizacion() != null) {
                q.setFechaFinalizacionFmt(q.getFechaFinalizacion().format(fmt));
            }
        }

        System.out.println("[DEBUG] Cargando página principal:");
        System.out.println("[DEBUG] - Quehaceres: " + listaQuehaceres.size());
        System.out.println("[DEBUG] - Miembros: " + listaMiembros.size());

        // Calcular estadísticas del hogar
        long tareasCompletadas = listaQuehaceres.stream().filter(Quehacer::isEstadoCompletado).count();
        long tareasPendientes = listaQuehaceres.stream().filter(q -> !q.isEstadoCompletado() && !q.isEstadoFinalizado()).count();
        long tareasVencidas = listaQuehaceres.stream().filter(q -> q.estaVencido() && !q.isEstadoFinalizado()).count();

        // Encontrar el miembro con más puntos (MVP del hogar)
        MiembroHogar mvpMiembro = null;
        int maxPuntos = 0;
        for (MiembroHogar miembro : listaMiembros) {
            if (miembro.getPuntos() > maxPuntos) {
                maxPuntos = miembro.getPuntos();
                mvpMiembro = miembro;
            }
        }

        // Top de cada liga
        MiembroHogar topBronce = null, topPlata = null, topOro = null;
        for (MiembroHogar m : listaMiembros) {
            if (m.getLiga() == null) continue;
            switch (m.getLiga()) {
                case BRONCE:
                    if (topBronce == null || m.getPuntos() > topBronce.getPuntos()) topBronce = m;
                    break;
                case PLATA:
                    if (topPlata == null || m.getPuntos() > topPlata.getPuntos()) topPlata = m;
                    break;
                case ORO:
                    if (topOro == null || m.getPuntos() > topOro.getPuntos()) topOro = m;
                    break;
            }
        }

        // Pasar datos al JSP
        request.setAttribute("listaQuehaceres", listaQuehaceres);
        request.setAttribute("listaMiembros", listaMiembros);
        request.setAttribute("tareasCompletadas", tareasCompletadas);
        request.setAttribute("tareasPendientes", tareasPendientes);
        request.setAttribute("tareasVencidas", tareasVencidas);
        request.setAttribute("mvpMiembro", mvpMiembro);
        request.setAttribute("topBronce", topBronce);
        request.setAttribute("topPlata", topPlata);
        request.setAttribute("topOro", topOro);

        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}