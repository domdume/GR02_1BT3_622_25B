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
            action = "list";
        }

        switch (action) {
            case "byMiembro":
                showIncentivosByMiembro(request, response);
                break;
            default:
                listAllIncentivos(request, response);
                break;
        }
    }

    private void listAllIncentivos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Incentivo> listaIncentivos = incentivoDAO.findAll();
        request.setAttribute("listaIncentivos", listaIncentivos);
        request.getRequestDispatcher("/incentivos/index.jsp").forward(request, response);
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