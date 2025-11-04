package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.JpaAchievementRepository;
import service.LogroRachaService;
import service.ServicioRacha;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@WebServlet(name = "StreakAchievementServlet", value = "/logros/racha/verify")
public class StreakAchievementServlet extends HttpServlet {

    private ServicioRacha servicioRacha;
    private LogroRachaService logroRachaService;

    @Override
    public void init() throws ServletException {
        super.init();
        servicioRacha = new ServicioRacha();
        logroRachaService = new LogroRachaService(new JpaAchievementRepository());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String midStr = req.getParameter("miembroId");
        if (midStr == null || midStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/rachas");
            return;
        }
        Long miembroId;
        try { miembroId = Long.parseLong(midStr); } catch (NumberFormatException nfe) {
            resp.sendRedirect(req.getContextPath() + "/rachas");
            return;
        }

        // Obtener rachas actuales y extraer la del miembro
        ServicioRacha.RachaData data = servicioRacha.obtenerRachasActuales();
        Map<Long, Integer> rachaMap = data.rachaPorMiembro;
        int rachaActual = rachaMap.getOrDefault(miembroId, 0);

        Optional<String> notificacion = logroRachaService.verificarYAsignar(miembroId, rachaActual);
        if (notificacion.isPresent()) {
            String logroId = rachaActual >= 7 ? LogroRachaService.LOGRO_RACHA_7 : LogroRachaService.LOGRO_RACHA_3;
            req.getSession().setAttribute("achievementMessage", notificacion.get());
            req.getSession().setAttribute("achievementLogroId", logroId);
        }

        // Redirigir a la pantalla de rachas (u otra) para mostrar el toast
        resp.sendRedirect(req.getContextPath() + "/rachas");
    }
}

