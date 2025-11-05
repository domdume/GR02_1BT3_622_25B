package servlet;
import model.*;
import dao.QuehacerDAO;
import dao.MiembroHogarDAO;
import service.HogarService;
import service.LogroService; // Importar el nuevo servicio de logros

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Logger;

@WebServlet(name = "QuehacerServlet", value = "/quehaceres")
public class QuehacerServlet extends HttpServlet {
    private QuehacerDAO quehacerDAO;
    private MiembroHogarDAO miembroHogarDAO;
    private HogarService hogarService;
    private LogroService logroService; // <<<--- 1. DECLARACIÓN DEL SERVICIO DE LOGROS

    private static final Logger logger = Logger.getLogger(QuehacerServlet.class.getName());

    @Override
    public void init() {
        quehacerDAO = new QuehacerDAO();
        miembroHogarDAO = new MiembroHogarDAO();
        hogarService = new HogarService();
        logroService = new LogroService(); // <<<--- 2. INICIALIZACIÓN DEL SERVICIO
        testFindAllMiembros();
    }

    // ... (El resto de los métodos doGet y los métodos privados que no cambian permanecen igual)
    // ... (doGet, doPost, showNewForm, insertQuehacer, deleteQuehacer, listQuehaceres, etc.)
    // Los métodos que cambian son listGestionQuehaceres y markComplete.

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

            case "delete":
                deleteQuehacer(request, response);
                break;
            case "complete":
                showCompleteForm(request, response);
                break;
            case "pending":
                showPendingForm(request, response);
                break;
            case "listGestion":
                listGestionQuehaceres(request, response);
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

            case "markComplete":
                markComplete(request, response);
                break;
            default:
                listQuehaceres(request, response);
                break;
        }
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll(); // Cargar miembros desde la BD
            System.out.println("[DEBUG] Lista de miembros recuperada: " + listaMiembros);
            logger.info("Lista de miembros recuperada: " + listaMiembros);

            // Cargar también la lista de quehaceres existentes para mostrar en la tabla
            List<Quehacer> todosLosQuehaceres = quehacerDAO.findAllWithMiembroHogar();
            System.out.println("[DEBUG] Lista de quehaceres recuperada: " + todosLosQuehaceres.size() + " elementos");

            // Aplicar lógica de puntos progresivos
            Map<String, Integer> puntosProgresivos = new HashMap<>();
            List<Quehacer> quehaceresCompletados = new ArrayList<>();
            List<Quehacer> quehaceresPendientes = new ArrayList<>();

            for (Quehacer q : todosLosQuehaceres) {
                // Clasificar por estado explícito para separar COMPLETADO / VENCIDO / PENDIENTE
                if (q.getEstado() == EstadoQuehacer.COMPLETADO) {
                    quehaceresCompletados.add(q);
                } else if (q.getEstado() == EstadoQuehacer.VENCIDO) {

                    quehaceresPendientes.add(q);
                } else {
                    quehaceresPendientes.add(q);
                }
            }

            // Ordenar los completados por fecha de finalización
            quehaceresCompletados.sort((q1, q2) -> {
                if (q1.getFechaFinalizacion() == null && q2.getFechaFinalizacion() == null) return 0;
                if (q1.getFechaFinalizacion() == null) return 1;
                if (q2.getFechaFinalizacion() == null) return -1;
                return q1.getFechaFinalizacion().compareTo(q2.getFechaFinalizacion());
            });

            // Calcular puntos progresivos
            for (Quehacer q : quehaceresCompletados) {
                if (q.getMiembroHogar() != null) {
                    String nombreMiembro = q.getMiembroHogar().getNombre();
                    int puntosActuales = puntosProgresivos.getOrDefault(nombreMiembro, 0);
                    int puntosDificultad = switch (q.getDificultad()) {
                        case FACIL -> 10;
                        case MEDIO -> 10;
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

            // Recombinar las listas
            List<Quehacer> listaQuehaceres = new ArrayList<>();
            listaQuehaceres.addAll(quehaceresCompletados);
            listaQuehaceres.addAll(quehaceresPendientes);

            if (listaMiembros.isEmpty()) {
                System.out.println("[DEBUG] No hay miembros disponibles");
                logger.warning("No hay miembros disponibles para asignar quehaceres.");
                request.getSession().setAttribute("errorMessage", "No hay miembros disponibles para asignar quehaceres.");
            } else {
                System.out.println("[DEBUG] Miembros disponibles: " + listaMiembros.size());
                logger.info("Miembros disponibles: " + listaMiembros.size());
                request.getSession().removeAttribute("errorMessage"); // Eliminar mensaje de error si hay miembros
            }

            request.setAttribute("listaMiembros", listaMiembros); // Pasar miembros al JSP
            request.setAttribute("listaQuehaceres", listaQuehaceres); // Pasar quehaceres al JSP
            System.out.println("[DEBUG] Atributos listaMiembros y listaQuehaceres establecidos en request");
            request.getRequestDispatcher("/quehaceres/form.jsp").forward(request, response); // Redirigir al JSP

        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            request.setAttribute("errorMessage", "Error interno del servidor: " + e.getMessage());
            request.getRequestDispatcher("/quehaceres/form.jsp").forward(request, response);
        }
    }

    private void insertQuehacer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombre = request.getParameter("nombre");
        String tiempoLimiteStr = request.getParameter("tiempoLimite");
        String miembroIdStr = request.getParameter("miembroId");
        String dificultadStr = request.getParameter("dificultad");

        System.out.println("[DEBUG] Insertando quehacer:");
        System.out.println("[DEBUG] - Nombre: " + nombre);
        System.out.println("[DEBUG] - Tiempo límite: " + tiempoLimiteStr);
        System.out.println("[DEBUG] - Miembro ID: " + miembroIdStr);
        System.out.println("[DEBUG] - Dificultad: " + dificultadStr);

        try {
            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty()) {
                request.getSession().setAttribute("errorMessage", "El nombre del quehacer es requerido.");
                response.sendRedirect(request.getContextPath() + "/quehaceres?action=new");
                return;
            }
            if (tiempoLimiteStr == null || tiempoLimiteStr.trim().isEmpty()) {
                request.getSession().setAttribute("errorMessage", "La fecha/hora límite es requerida.");
                response.sendRedirect(request.getContextPath() + "/quehaceres?action=new");
                return;
            }
            LocalDateTime tiempoLimite;
            try {
                tiempoLimite = LocalDateTime.parse(tiempoLimiteStr);
            } catch (Exception ex) {
                request.getSession().setAttribute("errorMessage", "El formato de fecha/hora límite es inválido. Usa yyyy-MM-ddTHH:mm:ss");
                response.sendRedirect(request.getContextPath() + "/quehaceres?action=new");
                return;
            }

            // Validar formato de tiempo límite
            if (tiempoLimite.isBefore(LocalDateTime.now())) {
                request.getSession().setAttribute("errorMessage", "El tiempo límite debe ser una fecha futura.");
                response.sendRedirect(request.getContextPath() + "/quehaceres?action=new");
                return;
            }

            // Validar dificultad
            model.Dificultad dificultad = model.Dificultad.MEDIO;
            if (dificultadStr != null && !dificultadStr.isEmpty()) {
                try {
                    dificultad = model.Dificultad.valueOf(dificultadStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    request.getSession().setAttribute("errorMessage", "La dificultad seleccionada es inválida.");
                    response.sendRedirect(request.getContextPath() + "/quehaceres?action=new");
                    return;
                }
            }

            // Validar miembro
            if (miembroIdStr == null || miembroIdStr.trim().isEmpty()) {
                request.getSession().setAttribute("errorMessage", "Debes seleccionar un miembro para asignar el quehacer.");
                response.sendRedirect(request.getContextPath() + "/quehaceres?action=new");
                return;
            }

            // Delegar la creación y persistencia al servicio (evitar duplicados)
            System.out.println("[QuehacerServlet] Llamando a hogarService.organizarQuehacer()");
            hogarService.organizarQuehacer(nombre.trim(), tiempoLimite, dificultad, miembroIdStr);
            System.out.println("[DEBUG] Quehacer creado exitosamente a través de HogarService");

            request.getSession().setAttribute("successMessage", "Quehacer agregado correctamente.");
        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            request.getSession().setAttribute("errorMessage", "Error al agregar el quehacer: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/quehaceres?action=list");
    }

    private void deleteQuehacer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        quehacerDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/quehaceres?action=list");
    }

    private void listQuehaceres(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("=== CARGANDO TABLERO PRINCIPAL ===");

        // Finalizar automáticamente las tareas vencidas
        finalizarTareasVencidas();

        // Cargar lista de quehaceres con miembros
        List<Quehacer> todosLosQuehaceres = quehacerDAO.findAllWithMiembroHogar();
        System.out.println("[DEBUG] Cargando " + todosLosQuehaceres.size() + " quehaceres para el tablero principal");

        // Calcular puntos progresivos: ordenar por fecha de finalización y calcular puntos acumulados
        Map<String, Integer> puntosProgresivos = new HashMap<>();

        // Separar completados, vencidos y pendientes
        List<Quehacer> quehaceresCompletados = new ArrayList<>();
        List<Quehacer> quehaceresVencidos = new ArrayList<>();
        List<Quehacer> quehaceresPendientes = new ArrayList<>();

        for (Quehacer q : todosLosQuehaceres) {
            if (q.getEstado() == EstadoQuehacer.COMPLETADO) {
                quehaceresCompletados.add(q);
            } else if (q.getEstado() == EstadoQuehacer.VENCIDO) {
                quehaceresVencidos.add(q);
            } else {
                quehaceresPendientes.add(q);
            }
        }

        // Ordenar los completados por fecha de finalización
        quehaceresCompletados.sort((q1, q2) -> {
            if (q1.getFechaFinalizacion() == null && q2.getFechaFinalizacion() == null) return 0;
            if (q1.getFechaFinalizacion() == null) return 1;
            if (q2.getFechaFinalizacion() == null) return -1;
            return q1.getFechaFinalizacion().compareTo(q2.getFechaFinalizacion());
        });

        // Calcular puntos progresivos para cada quehacer completado
        for (Quehacer q : quehaceresCompletados) {
            if (q.getMiembroHogar() != null) {
                String nombreMiembro = q.getMiembroHogar().getNombre();
                int puntosActuales = puntosProgresivos.getOrDefault(nombreMiembro, 0);

                int puntosDificultad = switch (q.getDificultad()) {
                    case FACIL -> 10;
                    case MEDIO -> 10;
                    case DIFICIL -> 30;
                };
                if (q.isCompletado() && q.fueCompletadoATiempo()) {
                    puntosActuales += puntosDificultad; // Sumar según dificultad
                } else {
                    puntosActuales = Math.max(0, puntosActuales - puntosDificultad); // Restar según dificultad
                }

                puntosProgresivos.put(nombreMiembro, puntosActuales);

                // Crear un atributo especial para este quehacer con sus puntos en ese momento
                q.setPuntosEnEseMomento(puntosActuales);
                System.out.println("[DEBUG] " + nombreMiembro + " tenía " + puntosActuales + " puntos después de " + q.getNombre());
            }
        }

        // Para pendientes, mostrar puntos actuales del miembro
        for (Quehacer q : quehaceresPendientes) {
            if (q.getMiembroHogar() != null) {
                q.setPuntosEnEseMomento(q.getMiembroHogar().getPuntos());
            }
        }

        // Recombinar las listas: primero completados, luego vencidos, luego pendientes
        List<Quehacer> listaQuehaceres = new ArrayList<>();
        listaQuehaceres.addAll(quehaceresCompletados);
        listaQuehaceres.addAll(quehaceresVencidos);
        listaQuehaceres.addAll(quehaceresPendientes);

        // Formateo de fechas para vista
        for (Quehacer q : listaQuehaceres) {
            if (q.getTiempoLimite() != null) {
                // Formateo directo sin usar métodos eliminados
                q.setTiempoLimite(q.getTiempoLimite());
            }
            if (q.getFechaFinalizacion() != null) {
                q.setFechaFinalizacion(q.getFechaFinalizacion());
            }
            // Establecer bandera vencido para la vista (evita llamadas a métodos EL)
            q.setVencido(q.estaVencido());
            // Asegurar que se muestre el puntaje actual del miembro tras penalizaciones
            if (q.getMiembroHogar() != null) {
                q.setPuntosEnEseMomento(q.getMiembroHogar().getPuntos());
            }
        }

        // Log detallado de los quehaceres cargados
        for (Quehacer q : listaQuehaceres) {
            System.out.println("[DEBUG] - Quehacer: " + q.getNombre() + " | Asignado a: " +
                    (q.getMiembroHogar() != null ? q.getMiembroHogar().getNombre() : "SIN ASIGNAR") +
                    " | Estado: " + q.getEstado() +
                    " | Puntos en ese momento: " + q.getPuntosEnEseMomento());
        }

        request.setAttribute("listaQuehaceres", listaQuehaceres);
        System.out.println("[DEBUG] Redirigiendo a tablero.jsp");
        request.getRequestDispatcher("/tablero.jsp").forward(request, response);
    }

    private void listGestionQuehaceres(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("=== CARGANDO GESTIÓN DE QUEHACERES ===");

        // Finalizar automáticamente las tareas vencidas
        finalizarTareasVencidas();

        // Cargar lista de quehaceres con miembros
        List<Quehacer> todosLosQuehaceres = quehacerDAO.findAllWithMiembroHogar();
        System.out.println("[DEBUG] Cargando " + todosLosQuehaceres.size() + " quehaceres para gestión");

        // Calcular puntos progresivos: ordenar por fecha de finalización y calcular puntos acumulados
        Map<String, Integer> puntosProgresivos = new HashMap<>();
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
        request.setAttribute("listaMiembros", listaMiembros); // ¡ESTO FALTABA!


        // Separar completados, vencidos y pendientes
        List<Quehacer> quehaceresCompletados = new ArrayList<>();
        List<Quehacer> quehaceresVencidos = new ArrayList<>();
        List<Quehacer> quehaceresPendientes = new ArrayList<>();

        // Variables para estadísticas (movidas desde JSP)
        int totalTareas = todosLosQuehaceres.size();
        int tareasCompletadas = 0;
        int tareasPendientes = 0;
        int tareasVencidas = 0;

        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        for (Quehacer q : todosLosQuehaceres) {
            if (q.getEstado() == EstadoQuehacer.VENCIDO) {
                quehaceresVencidos.add(q);
                tareasVencidas++;
            } else if (q.getEstado() == EstadoQuehacer.COMPLETADO) {
                quehaceresCompletados.add(q);
                tareasCompletadas++;
            } else {
                // Estado pendiente
                // Si por algún motivo el tiempo límite ya pasó y la tarea no fue marcada, la contamos como vencida
                if (q.getTiempoLimite() != null && q.getTiempoLimite().isBefore(ahora)) {
                    quehaceresVencidos.add(q);
                    tareasVencidas++;
                } else {
                    quehaceresPendientes.add(q);
                    tareasPendientes++;
                }
            }
        }

        // Ordenar los completados por fecha de finalización
        quehaceresCompletados.sort((q1, q2) -> {
            if (q1.getFechaFinalizacion() == null && q2.getFechaFinalizacion() == null) return 0;
            if (q1.getFechaFinalizacion() == null) return 1;
            if (q2.getFechaFinalizacion() == null) return -1;
            return q1.getFechaFinalizacion().compareTo(q2.getFechaFinalizacion());
        });

        // Calcular puntos progresivos para cada quehacer completado
        for (Quehacer q : quehaceresCompletados) {
            MiembroHogar miembro = q.getMiembroHogar();
            if (miembro != null) {
                String nombreMiembro = miembro.getNombre();
                int puntosActuales = puntosProgresivos.getOrDefault(nombreMiembro, 0);

                // Usar la misma lógica de puntos que en Incentivo
                int puntosDificultad = switch (q.getDificultad()) {
                    case FACIL -> 10;
                    case MEDIO -> 10;
                    case DIFICIL -> 30;
                };
                if (q.fueCompletadoATiempo()) {
                    puntosActuales += puntosDificultad;
                } else {
                    puntosActuales = Math.max(0, puntosActuales - puntosDificultad); // Restar según dificultad
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

        // Recombinar las listas: primero completados (ordenados por fecha), luego vencidos, luego pendientes
        List<Quehacer> listaQuehaceres = new ArrayList<>();
        listaQuehaceres.addAll(quehaceresCompletados);
        listaQuehaceres.addAll(quehaceresVencidos);
        listaQuehaceres.addAll(quehaceresPendientes);

        // Formateo de fechas para vista
        for (Quehacer q : listaQuehaceres) {
            if (q.getTiempoLimite() != null) {
                // Formateo directo sin usar métodos eliminados
                q.setTiempoLimite(q.getTiempoLimite());
            }
            if (q.getFechaFinalizacion() != null) {
                q.setFechaFinalizacion(q.getFechaFinalizacion());
            }
            // Establecer bandera vencido para la vista (evita llamadas a métodos EL)
            q.setVencido(q.estaVencido());
            if (q.getMiembroHogar() != null) {
                q.setPuntosEnEseMomento(q.getMiembroHogar().getPuntos());
            }
        }



        // Pasar datos y estadísticas calculadas a la vista (no calcular en JSP)
        request.setAttribute("listaQuehaceres", listaQuehaceres);
        request.setAttribute("totalTareas", totalTareas);
        request.setAttribute("tareasCompletadas", tareasCompletadas);
        request.setAttribute("tareasPendientes", tareasPendientes);
        request.setAttribute("tareasVencidas", tareasVencidas);

        request.getRequestDispatcher("/quehaceres/index.jsp").forward(request, response);
    }

    private void showPendingForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Finalizar automáticamente las tareas vencidas
            finalizarTareasVencidas();

            // Cargar lista de miembros
            List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
            System.out.println("[DEBUG] Lista de miembros para pendientes: " + listaMiembros);

            // Verificar si se seleccionó un miembro específico
            String miembroIdStr = request.getParameter("miembroId");
            List<Quehacer> tareasPendientes = new java.util.ArrayList<>();

            if (miembroIdStr != null && !miembroIdStr.isEmpty()) {
                try {
                    Long miembroId = Long.parseLong(miembroIdStr);
                    MiembroHogar miembroSeleccionado = miembroHogarDAO.findById(miembroId);

                    if (miembroSeleccionado != null) {
                        // Obtener todas las tareas y filtrar las del miembro que están realmente pendientes
                        List<Quehacer> todasLasTareas = quehacerDAO.findAll();
                        LocalDateTime ahora = LocalDateTime.now();
                        for (Quehacer q : todasLasTareas) {
                            if (q.getMiembroHogar() != null &&
                                    q.getMiembroHogar().getId().equals(miembroId) &&
                                    q.getEstado() == model.EstadoQuehacer.PENDIENTE &&
                                    q.getTiempoLimite() != null && ahora.isBefore(q.getTiempoLimite())) { // Solo las que no han vencido
                                if (q.getTiempoLimite() != null) q.setTiempoLimite(q.getTiempoLimite());
                                if (q.getFechaFinalizacion() != null) q.setFechaFinalizacion(q.getFechaFinalizacion());
                                tareasPendientes.add(q);
                            }
                        }
                        System.out.println("[DEBUG] Tareas pendientes (no vencidas) para miembro " + miembroSeleccionado.getNombre() + ": " + tareasPendientes);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("[DEBUG] Error al parsear miembroId: " + e.getMessage());
                }
            }

            request.setAttribute("listaMiembros", listaMiembros);
            request.setAttribute("tareasPendientes", tareasPendientes);
            request.setAttribute("miembroSeleccionado", miembroIdStr);
            request.getRequestDispatcher("/quehaceres/pending.jsp").forward(request, response);

        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            request.setAttribute("errorMessage", "Error interno del servidor: " + e.getMessage());
            request.getRequestDispatcher("/quehaceres/pending.jsp").forward(request, response);
        }
    }

    private void showCompleteForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Finalizar automáticamente las tareas vencidas
            finalizarTareasVencidas();

            // Cargar lista de miembros
            List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
            System.out.println("[DEBUG] Lista de miembros para completar: " + listaMiembros);

            // Cargar lista de quehaceres pendientes (no completados, no finalizados, y no vencidos)
            List<Quehacer> todosLosQuehaceres = quehacerDAO.findAll();
            List<Quehacer> listaQuehaceres = new java.util.ArrayList<>();
            java.time.LocalDateTime ahora = java.time.LocalDateTime.now();

            for (Quehacer q : todosLosQuehaceres) {
                // Solo mostrar quehaceres que están realmente pendientes (estado PENDIENTE y no vencidos)
                if (q.getEstado() == model.EstadoQuehacer.PENDIENTE && q.getTiempoLimite() != null && ahora.isBefore(q.getTiempoLimite())) {
                    if (q.getTiempoLimite() != null) q.setTiempoLimite(q.getTiempoLimite());
                    if (q.getFechaFinalizacion() != null) q.setFechaFinalizacion(q.getFechaFinalizacion());
                    listaQuehaceres.add(q);
                }
            }
            request.setAttribute("listaMiembros", listaMiembros);
            request.setAttribute("listaQuehaceres", listaQuehaceres);
            request.getRequestDispatcher("/quehaceres/complete.jsp").forward(request, response);

        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            request.setAttribute("errorMessage", "Error interno del servidor: " + e.getMessage());
            request.getRequestDispatcher("/quehaceres/complete.jsp").forward(request, response);
        }
    }

    private void markComplete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String quehacerIdStr = request.getParameter("quehacerId");
        String fechaFinalizacionStr = request.getParameter("fechaFinalizacion");

        System.out.println("[DEBUG] Marcando quehacer como completado:");
        System.out.println("[DEBUG] - Quehacer ID: " + quehacerIdStr);
        System.out.println("[DEBUG] - Fecha finalización: " + fechaFinalizacionStr);

        try {
            Long id = Long.parseLong(quehacerIdStr);
            Quehacer quehacer = quehacerDAO.findById(id);

            if (quehacer == null) {
                request.getSession().setAttribute("errorMessage", "Quehacer no encontrado");
                response.sendRedirect(request.getContextPath() + "/quehaceres?action=complete");
                return;
            }

            if (quehacer.isCompletado() || quehacer.isEstadoFinalizado()) {
                request.getSession().setAttribute("errorMessage", "Esta tarea ya está finalizada.");
                response.sendRedirect(request.getContextPath() + "/quehaceres?action=complete");
                return;
            }

            LocalDateTime fechaFinalizacion = parseDateTimeLenient(fechaFinalizacionStr);
            quehacer.setFechaFinalizacion(fechaFinalizacion);
            quehacer.setEstado(EstadoQuehacer.COMPLETADO);

            MiembroHogar miembro = null;
            if (quehacer.getMiembroHogar() != null && quehacer.getMiembroHogar().getId() != null) {
                miembro = miembroHogarDAO.findById(quehacer.getMiembroHogar().getId());
            }

            String mensajeLogro = ""; // Mensaje adicional para notificar sobre logros

            if (miembro != null) {
                // <<<--- 3. LÓGICA DE LOGROS ---
                // Guardar la liga ANTES de aplicar el incentivo
                Liga ligaAnterior = miembro.getLiga();
                Liga ligaNueva = miembro.getLiga();
                System.out.println("[DEBUG] Aplicando incentivo para miembro: " + miembro.getNombre());




                // Llamar al nuevo servicio para que gestione si se debe asignar un logro de ascenso.
                // El servicio se encarga de la lógica interna (si es el primer logro, si hubo ascenso, etc.)
                // 1) Incrementar contador de tareas completadas (criterio de aceptación 1)
                miembro.setTareasCompletadas(miembro.getTareasCompletadas() + 1);

                // 2) Verificar y asignar medallas por tareas completadas (criterio de aceptación 2)
                logroService.verificarLogroPorQuehaceres(miembro);

                // 3) Intentar asignar emblemas por ascenso de liga
                logroService.asignarEmblemaAscenso(miembro, ligaAnterior, ligaNueva);

                // 4) Aplicar incentivo que actualiza puntos y persiste
                Incentivo.aplicarIncentivo(miembro, quehacer);
                // Como el nuevo servicio no devuelve un mensaje específico, creamos uno genérico
                // si detectamos que el miembro ha subido de liga.
                if (ligaAnterior != null && ligaNueva != null && ligaNueva.getNivel() > ligaAnterior.getNivel()) {
                    mensajeLogro = " ¡Y ha ascendido a la liga " + ligaNueva + "!";
                    logger.info("Miembro " + miembro.getNombre() + " ascendió de " + ligaAnterior + " a " + ligaNueva);
                }
                // --- FIN LÓGICA DE LOGROS --->>>

                // Actualizar la base de datos
                quehacerDAO.update(quehacer);
                miembroHogarDAO.update(miembro);

            } else {
                // Si no hay miembro, solo se actualiza el quehacer
                quehacerDAO.update(quehacer);
            }

            // Preparar mensaje de éxito
            String nombreMiembro = (miembro != null) ? miembro.getNombre() : "desconocido";
            int puntosActuales = (miembro != null) ? miembro.getPuntos() : 0;
            int puntosDificultad = switch (quehacer.getDificultad()) {
                case FACIL, MEDIO -> 10;
                case DIFICIL -> 30;
            };
            int puntosGanados = quehacer.fueCompletadoATiempo() ? puntosDificultad : -puntosDificultad;

            // Construir el mensaje final, incluyendo la notificación del logro
            request.getSession().setAttribute("successMessage",
                    "¡Quehacer completado! " + nombreMiembro + " " + (puntosGanados >= 0 ? "ganó " : "perdió ") +
                            Math.abs(puntosGanados) + " puntos. Total actual: " + puntosActuales + " puntos." + mensajeLogro);

            System.out.println("[DEBUG] Quehacer actualizado exitosamente");

        } catch (Exception e) {
            logger.severe("Error al marcar como completado: " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace para un debug más detallado
            request.getSession().setAttribute("errorMessage", "Error al marcar el quehacer como completado: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/quehaceres?action=complete");
    }

    /**
     * Intenta parsear una cadena a LocalDateTime usando varios formatos tolerantes.
     * Lanzará DateTimeParseException si ninguno aplica.
     */
    private LocalDateTime parseDateTimeLenient(String input) {
        if (input == null) throw new DateTimeParseException("Null input", "", 0);
        String s = input.trim();
        // Intentar ISO primero
        List<DateTimeFormatter> fmts = Arrays.asList(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        );

        // Si viene con espacio y sin segundos, intentar añadir seconds? we'll try patterns above
        for (DateTimeFormatter fmt : fmts) {
            try {
                return LocalDateTime.parse(s, fmt);
            } catch (DateTimeParseException ignored) {
                // seguir intentando
            }
        }

        // Como último recurso, si contiene espacio en medio, reemplazamos por 'T' e intentamos ISO
        if (s.contains(" ")) {
            String t = s.replace(' ', 'T');
            try {
                return LocalDateTime.parse(t, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException ignored) {
            }
        }

        // Ningún formato aplicó -> lanzar excepción
        throw new DateTimeParseException("No matching format for date time", input, 0);
    }



    private void finalizarTareasVencidas() {
        // Delegar la operación al DAO para evitar duplicación de lógica y permitir reuso
        quehacerDAO.finalizeOverdueAndApplyPenalties();
    }

    public void testFindAllMiembros() {
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
        logger.info("Resultados de findAll: " + listaMiembros);
    }
}

