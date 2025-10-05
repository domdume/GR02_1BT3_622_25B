package servlet;

import dao.MiembroHogarDAO;
import dao.QuehacerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Dificultad;
import model.MiembroHogar;
import model.Quehacer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(name = "QuehacerServlet", value = "/quehaceres")
public class QuehacerServlet extends HttpServlet {
    private QuehacerDAO quehacerDAO;
    private MiembroHogarDAO miembroHogarDAO;

    public void init() {
        quehacerDAO = new QuehacerDAO();
        miembroHogarDAO = new MiembroHogarDAO();
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
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteQuehacer(request, response);
                break;
            default:
                listQuehaceres(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "insert":
                insertQuehacer(request, response);
                break;
            case "update":
                updateQuehacer(request, response);
                break;
            default:
                listQuehaceres(request, response);
                break;
        }
    }

    private void listQuehaceres(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Quehacer> listaQuehaceres = quehacerDAO.findAll();
        request.setAttribute("listaQuehaceres", listaQuehaceres);
        request.getRequestDispatcher("/quehaceres/index.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
        request.setAttribute("listaMiembros", listaMiembros);
        request.setAttribute("dificultades", Dificultad.values());
        request.getRequestDispatcher("/quehaceres/form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Quehacer quehacerExistente = quehacerDAO.findById(id);
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
        request.setAttribute("quehacer", quehacerExistente);
        request.setAttribute("listaMiembros", listaMiembros);
        request.setAttribute("dificultades", Dificultad.values());
        request.getRequestDispatcher("/quehaceres/form.jsp").forward(request, response);
    }

    private void insertQuehacer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombre = request.getParameter("nombre");
        Dificultad dificultad = Dificultad.valueOf(request.getParameter("dificultad"));
        LocalDateTime tiempoLimite = LocalDateTime.parse(request.getParameter("tiempoLimite"));
        Long miembroId = Long.parseLong(request.getParameter("miembroId"));

        MiembroHogar miembro = miembroHogarDAO.findById(miembroId);
        Quehacer nuevoQuehacer = new Quehacer(nombre, dificultad, tiempoLimite);
        nuevoQuehacer.setMiembroHogar(miembro);

        quehacerDAO.create(nuevoQuehacer);
        response.sendRedirect(request.getContextPath() + "/quehaceres");
    }

    private void updateQuehacer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        String nombre = request.getParameter("nombre");
        Dificultad dificultad = Dificultad.valueOf(request.getParameter("dificultad"));
        LocalDateTime tiempoLimite = LocalDateTime.parse(request.getParameter("tiempoLimite"));
        Long miembroId = Long.parseLong(request.getParameter("miembroId"));

        MiembroHogar miembro = miembroHogarDAO.findById(miembroId);
        Quehacer quehacer = quehacerDAO.findById(id);
        quehacer.setNombre(nombre);
        quehacer.setDificultad(dificultad);
        quehacer.setTiempoLimite(tiempoLimite);
        quehacer.setMiembroHogar(miembro);

        quehacerDAO.update(quehacer);
        response.sendRedirect(request.getContextPath() + "/quehaceres");
    }

    private void deleteQuehacer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        quehacerDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/quehaceres");
    }
}
