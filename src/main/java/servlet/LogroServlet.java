package servlet;

import dao.MiembroHogarDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.MiembroHogar;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogroServlet", value = "/logros")
public class LogroServlet extends HttpServlet {
    private MiembroHogarDAO miembroHogarDAO;

    public void init() {
        miembroHogarDAO = new MiembroHogarDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        listMiembros(request, response);
    }

    private void listMiembros(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
        request.setAttribute("listaMiembros", listaMiembros);
        request.getRequestDispatcher("/logros/index.jsp").forward(request, response);
    }
}
