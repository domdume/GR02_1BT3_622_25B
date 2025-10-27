package servlet;

import dao.MiembroHogarDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Liga;
import model.MiembroHogar;
import service.LigaService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "RankingServlet", value = "/ranking")
public class RankingServlet extends HttpServlet {
    private MiembroHogarDAO miembroDAO;
    private LigaService ligaService;

    @Override
    public void init() {
        miembroDAO = new MiembroHogarDAO();
        ligaService = new LigaService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MiembroHogar> miembros = miembroDAO.findAll();
        
        // Actualizar ligas basadas en puntos actuales antes de mostrar
        for (MiembroHogar m : miembros) {
            ligaService.actualizarLiga(m);
            // Persistir la liga actualizada
            miembroDAO.update(m);
        }
        
        // Orden general por puntos desc
        List<MiembroHogar> topGlobal = new ArrayList<>(miembros);
        topGlobal.sort(Comparator.comparingInt(MiembroHogar::getPuntos).reversed());

        // Agrupar por liga y ordenar por puntos desc
        Map<Liga, List<MiembroHogar>> porLiga = miembros.stream()
                .filter(m -> m.getLiga() != null)
                .collect(Collectors.groupingBy(MiembroHogar::getLiga));
        for (Map.Entry<Liga, List<MiembroHogar>> e : porLiga.entrySet()) {
            e.getValue().sort(Comparator.comparingInt(MiembroHogar::getPuntos).reversed());
        }

        request.setAttribute("topGlobal", topGlobal);
        request.setAttribute("porLiga", porLiga);
        request.getRequestDispatcher("/ranking/index.jsp").forward(request, response);
    }
}

