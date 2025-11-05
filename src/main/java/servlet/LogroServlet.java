package servlet;

import dao.MiembroHogarDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MiembroHogar;
import service.LogroService;
import service.ServicioRacha;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(name = "LogroServlet", value = "/logros")
public class LogroServlet extends HttpServlet {
    private MiembroHogarDAO miembroHogarDAO;
    private transient ServicioRacha servicioRacha;
    private transient LogroService logroService;

    public void init() {
        miembroHogarDAO = new MiembroHogarDAO();
        servicioRacha = new ServicioRacha();
        logroService = new LogroService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        listMiembros(request, response);
    }

    private void listMiembros(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1) Calcular rachas actuales para todos los miembros
        ServicioRacha.RachaData rachaData = servicioRacha.obtenerRachasActuales();
        Map<Long, Integer> rachaPorMiembro = rachaData.rachaPorMiembro;

        // 2) Verificar y asignar logros de racha si corresponde (solo una vez por logro)
        List<String> notificaciones = new ArrayList<>();
        for (MiembroHogar m : rachaData.miembrosOrdenados) {
            if (m == null || m.getId() == null) continue;
            int racha = rachaPorMiembro.getOrDefault(m.getId(), 0);
            logroService.verificarYAsignarLogroRachaConTipo(m.getId(), racha)
                    .ifPresent(n -> notificaciones.add("[" + n.getTipo() + "] " + n.getMensaje()));
        }
        if (!notificaciones.isEmpty()) {
            request.setAttribute("successMessage", String.join(" ", notificaciones));
        }

        // 3) Cargar miembros con sus logros actualizados para la vista
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
        request.setAttribute("listaMiembros", listaMiembros);
        request.getRequestDispatcher("/logros/index.jsp").forward(request, response);
    }
}
