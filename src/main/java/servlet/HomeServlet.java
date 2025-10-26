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
import model.EstadoQuehacer;
import service.LigaService;

import java.io.IOException;
import java.util.*;
import java.util.List;

@WebServlet(name = "HomeServlet", value = {"", "/home"})
public class HomeServlet extends HttpServlet {
    private QuehacerDAO quehacerDAO;
    private MiembroHogarDAO miembroHogarDAO;
    private LigaService ligaService;

    public void init() {
        quehacerDAO = new QuehacerDAO();
        miembroHogarDAO = new MiembroHogarDAO();
        ligaService = new LigaService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[DEBUG HomeServlet] Iniciando carga de dashboard");

        // Cargar datos para el tablero principal
        List<Quehacer> listaQuehaceres = quehacerDAO.findAllWithMiembroHogar();
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll(); // Ya usa LEFT JOIN FETCH

        System.out.println("[DEBUG HomeServlet] Quehaceres cargados: " + listaQuehaceres.size());
        System.out.println("[DEBUG HomeServlet] Miembros cargados: " + listaMiembros.size());

        // Actualizar ligas basadas en puntos actuales antes de calcular tops
        for (MiembroHogar m : listaMiembros) {
            ligaService.actualizarLiga(m);
            // Persistir la liga actualizada
            miembroHogarDAO.update(m);
        }

        // Calcular puntos progresivos para mostrar puntos acumulados en cada quehacer
        Map<String, Integer> puntosProgresivos = new HashMap<>();
        List<Quehacer> quehaceresCompletados = new ArrayList<>();
        List<Quehacer> quehaceresPendientes = new ArrayList<>();

        for (Quehacer q : listaQuehaceres) {
            if (q.getEstado() == EstadoQuehacer.COMPLETADO) {
                quehaceresCompletados.add(q);
            } else {
                quehaceresPendientes.add(q);
            }
        }

        // Ordenar completados por fecha de finalización
        quehaceresCompletados.sort((q1, q2) -> {
            if (q1.getFechaFinalizacion() == null && q2.getFechaFinalizacion() == null) return 0;
            if (q1.getFechaFinalizacion() == null) return 1;
            if (q2.getFechaFinalizacion() == null) return -1;
            return q1.getFechaFinalizacion().compareTo(q2.getFechaFinalizacion());
        });

        // Calcular puntos acumulados para completados
        for (Quehacer q : quehaceresCompletados) {
            if (q.getMiembroHogar() != null) {
                String nombreMiembro = q.getMiembroHogar().getNombre();
                int puntosActuales = puntosProgresivos.getOrDefault(nombreMiembro, 0);

                int puntosDificultad = switch (q.getDificultad()) {
                    case FACIL -> 10;
                    case MEDIO -> 20;
                    case DIFICIL -> 30;
                };
                if (q.fueCompletadoATiempo()) {
                    puntosActuales += puntosDificultad;
                } else {
                    puntosActuales = Math.max(0, puntosActuales - puntosDificultad);
                }

                puntosProgresivos.put(nombreMiembro, puntosActuales);
                q.setPuntosEnEseMomento(puntosActuales);
            }
        }

        // Para pendientes, mostrar puntos actuales del miembro
        for (Quehacer q : quehaceresPendientes) {
            if (q.getMiembroHogar() != null) {
                q.setPuntosEnEseMomento(q.getMiembroHogar().getPuntos());
            }
        }

        // Establecer bandera 'vencido' para la vista (evita llamadas a métodos desde EL)
        for (Quehacer q : listaQuehaceres) {
            q.setVencido(q.estaVencido());
        }

        System.out.println("[DEBUG] Cargando página principal:");
        System.out.println("[DEBUG] - Quehaceres: " + listaQuehaceres.size());
        System.out.println("[DEBUG] - Miembros: " + listaMiembros.size());

        // Calcular estadísticas del hogar
    long tareasCompletadas = listaQuehaceres.stream().filter(q -> q.getEstado() == EstadoQuehacer.COMPLETADO).count();
    long tareasPendientes = listaQuehaceres.stream().filter(q -> q.getEstado() == EstadoQuehacer.PENDIENTE && !q.estaVencido()).count();
    // Contar vencidas tanto si el estado es VENCIDO en la DB como si la fecha límite ya pasó
    long tareasVencidas = listaQuehaceres.stream().filter(q -> q.getEstado() == EstadoQuehacer.VENCIDO || q.estaVencido()).count();

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
