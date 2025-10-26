package servlet;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import service.MiembroHogarService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Liga;
import model.MiembroHogar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "RankingServlet", value = "/ranking")
public class RankingServlet extends HttpServlet {
    private MiembroHogarService miembroHogarService;

    @Override
    public void init() {
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        this.miembroHogarService = ctx.getBean(MiembroHogarService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MiembroHogar> miembros = miembroHogarService.obtenerTodos();
        List<MiembroHogar> topGlobal = new ArrayList<>(miembros);
        topGlobal.sort(Comparator.comparingInt(MiembroHogar::getPuntos).reversed());

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

