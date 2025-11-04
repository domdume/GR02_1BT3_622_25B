//package servlet;
//
//import dao.MiembroHogarDAO;
//import dao.QuehacerDAO;
//import service.HogarService;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import model.Dificultad;
//import model.Incentivo;
//import model.MiembroHogar;
//import model.Quehacer;
//import model.EstadoQuehacer;
//import model.Liga;
//import service.EmblemaService;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.logging.Logger;
//
//@WebServlet(name = "QuehacerServlet", value = "/quehaceres")
//public class QuehacerServlet extends HttpServlet {
//    private QuehacerDAO quehacerDAO;
//    private MiembroHogarDAO miembroHogarDAO;
//    private HogarService hogarService;
//
//    private static final Logger logger = Logger.getLogger(QuehacerServlet.class.getName());
//
//    @Override
//    public void init() {
//        quehacerDAO = new QuehacerDAO();
//        miembroHogarDAO = new MiembroHogarDAO();
//        hogarService = new HogarService();
//        testFindAllMiembros();
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String action = request.getParameter("action");
//        if (action == null) {
//            action = "list";
//        }
//
//        switch (action) {
//            case "new":
//                showNewForm(request, response);
//                break;
//
//            case "delete":
//                deleteQuehacer(request, response);
//                break;
//            case "complete":
//                showCompleteForm(request, response);
//                break;
//            case "pending":
//                showPendingForm(request, response);
//                break;
//            case "listGestion":
//                listGestionQuehaceres(request, response);
//                break;
//            default:
//                listQuehaceres(request, response);
//                break;
//        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String action = request.getParameter("action");
//        if (action == null) {
//            action = "list";
//        }
//
//        switch (action) {
//            case "insert":
//                insertQuehacer(request, response);
//                break;
//
//            case "markComplete":
//                markComplete(request, response);
//                break;
//            default:
//                listQuehaceres(request, response);
//                break;
//        }
//    }
//
//    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        try {
//            List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll(); // Cargar miembros desde la BD
//            System.out.println("[DEBUG] Lista de miembros recuperada: " + listaMiembros);
//            logger.info("Lista de miembros recuperada: " + listaMiembros);
//
//            // Cargar también la lista de quehaceres existentes para mostrar en la tabla
//            List<Quehacer> todosLosQuehaceres = quehacerDAO.findAllWithMiembroHogar();
//            System.out.println("[DEBUG] Lista de quehaceres recuperada: " + todosLosQuehaceres.size() + " elementos");
//
//            // Aplicar lógica de puntos progresivos
//            Map<String, Integer> puntosProgresivos = new HashMap<>();
//            List<Quehacer> quehaceresCompletados = new ArrayList<>();
//            List<Quehacer> quehaceresPendientes = new ArrayList<>();
//
//            for (Quehacer q : todosLosQuehaceres) {
//                if (q.isEstadoFinalizado()) {
//                    quehaceresCompletados.add(q);
//                } else {
//                    quehaceresPendientes.add(q);
//                }
//            }
//
//            // Ordenar los completados por fecha de finalización
//            quehaceresCompletados.sort((q1, q2) -> {
//                if (q1.getFechaFinalizacion() == null && q2.getFechaFinalizacion() == null) return 0;
//                if (q1.getFechaFinalizacion() == null) return 1;
//                if (q2.getFechaFinalizacion() == null) return -1;
//                return q1.getFechaFinalizacion().compareTo(q2.getFechaFinalizacion());
//            });
//
//            // Calcular puntos progresivos
//            for (Quehacer q : quehaceresCompletados) {
//                if (q.getMiembroHogar() != null) {
//                    String nombreMiembro = q.getMiembroHogar().getNombre();
//                    int puntosActuales = puntosProgresivos.getOrDefault(nombreMiembro, 0);
//
//                    if (q.isCompletado()) {
//                        puntosActuales += 20;
//                    } else {
//                        puntosActuales = Math.max(0, puntosActuales - 10);
//                    }
//
//                    puntosProgresivos.put(nombreMiembro, puntosActuales);
//                    q.setPuntosEnEseMomento(puntosActuales);
//                }
//            }
//
//            // Para pendientes, mostrar puntos actuales del miembro
//            for (Quehacer q : quehaceresPendientes) {
//                if (q.getMiembroHogar() != null) {
//                    q.setPuntosEnEseMomento(q.getMiembroHogar().getPuntos());
//                }
//            }
//
//            // Recombinar las listas
//            List<Quehacer> listaQuehaceres = new ArrayList<>();
//            listaQuehaceres.addAll(quehaceresCompletados);
//            listaQuehaceres.addAll(quehaceresPendientes);
//
//            if (listaMiembros.isEmpty()) {
//                System.out.println("[DEBUG] No hay miembros disponibles");
//                logger.warning("No hay miembros disponibles para asignar quehaceres.");
//                request.getSession().setAttribute("errorMessage", "No hay miembros disponibles para asignar quehaceres.");
//            } else {
//                System.out.println("[DEBUG] Miembros disponibles: " + listaMiembros.size());
//                logger.info("Miembros disponibles: " + listaMiembros.size());
//                request.getSession().removeAttribute("errorMessage"); // Eliminar mensaje de error si hay miembros
//            }
//
//            request.setAttribute("listaMiembros", listaMiembros); // Pasar miembros al JSP
//            request.setAttribute("listaQuehaceres", listaQuehaceres); // Pasar quehaceres al JSP
//            System.out.println("[DEBUG] Atributos listaMiembros y listaQuehaceres establecidos en request");
//            request.getRequestDispatcher("/quehaceres/form.jsp").forward(request, response); // Redirigir al JSP
//
//        } catch (Exception e) {
//            System.out.println("[ERROR] Error en showNewForm: " + e.getMessage());
//            e.printStackTrace();
//            request.setAttribute("errorMessage", "Error interno del servidor: " + e.getMessage());
//            request.getRequestDispatcher("/quehaceres/form.jsp").forward(request, response);
//        }
//    }
//
//    private void insertQuehacer(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String nombre = request.getParameter("nombre");
//        String tiempoLimiteStr = request.getParameter("tiempoLimite");
//        String miembroIdStr = request.getParameter("miembroId");
//        String dificultadStr = request.getParameter("dificultad");
//
//        System.out.println("[DEBUG] Insertando quehacer:");
//        System.out.println("[DEBUG] - Nombre: " + nombre);
//        System.out.println("[DEBUG] - Tiempo límite: " + tiempoLimiteStr);
//        System.out.println("[DEBUG] - Miembro ID: " + miembroIdStr);
//        System.out.println("[DEBUG] - Dificultad: " + dificultadStr);
//
//        try {
//            Long miembroId = Long.parseLong(miembroIdStr);
//            MiembroHogar miembro = miembroHogarDAO.findById(miembroId);
//            System.out.println("[DEBUG] - Miembro encontrado: " + (miembro != null ? miembro.getNombre() : "null"));
//
//            LocalDateTime tiempoLimite = LocalDateTime.parse(tiempoLimiteStr);
//
//            // Usar dificultad del formulario o MEDIO por defecto
//            Dificultad dificultad = Dificultad.MEDIO; // Valor por defecto
//            if (dificultadStr != null && !dificultadStr.isEmpty()) {
//                try {
//                    dificultad = Dificultad.valueOf(dificultadStr.toUpperCase());
//                } catch (IllegalArgumentException e) {
//                    System.out.println("[DEBUG] Dificultad inválida, usando MEDIO por defecto");
//                }
//
//            System.out.println("[QuehacerServlet] Llamando a hogarService.organizarQuehacer()");
//            hogarService.organizarQuehacer(nombre, tiempoLimite, dificultad, miembroIdStr);
//            System.out.println("[DEBUG] Quehacer creado exitosamente a través de HogarService");
//
//            }
//
//            // Usar el nuevo constructor con dificultad según el diagrama UML
//            Quehacer nuevoQuehacer = new Quehacer(nombre, tiempoLimite, dificultad);
//            nuevoQuehacer.setMiembroHogar(miembro);
//
//            quehacerDAO.create(nuevoQuehacer);
//            System.out.println("[DEBUG] Quehacer creado exitosamente");
//            request.getSession().setAttribute("successMessage", "Quehacer agregado correctamente.");
//        } catch (Exception e) {
//            System.out.println("[DEBUG] Error al crear quehacer: " + e.getMessage());
//            e.printStackTrace();
//            request.getSession().setAttribute("errorMessage", "Error al agregar el quehacer: " + e.getMessage());
//        }
//
//        response.sendRedirect(request.getContextPath() + "/quehaceres?action=listGestion");
//    }
//
//    private void deleteQuehacer(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        Long id = Long.parseLong(request.getParameter("id"));
//        quehacerDAO.delete(id);
//        response.sendRedirect(request.getContextPath() + "/quehaceres?action=listGestion");
//    }
//
//    private void listQuehaceres(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        System.out.println("=== CARGANDO TABLERO PRINCIPAL ===");
//
//        // Finalizar automáticamente las tareas vencidas
//        finalizarTareasVencidas();
//
//        // Cargar lista de quehaceres con miembros
//        List<Quehacer> todosLosQuehaceres = quehacerDAO.findAllWithMiembroHogar();
//        System.out.println("[DEBUG] Cargando " + todosLosQuehaceres.size() + " quehaceres para el tablero principal");
//
//        // Calcular puntos progresivos: ordenar por fecha de finalización y calcular puntos acumulados
//        Map<String, Integer> puntosProgresivos = new HashMap<>();
//
//        // Separar completados y pendientes
//        List<Quehacer> quehaceresCompletados = new ArrayList<>();
//        List<Quehacer> quehaceresPendientes = new ArrayList<>();
//
//        for (Quehacer q : todosLosQuehaceres) {
//            if (q.isEstadoFinalizado()) {
//                quehaceresCompletados.add(q);
//            } else {
//                quehaceresPendientes.add(q);
//            }
//        }
//
//        // Ordenar los completados por fecha de finalización
//        quehaceresCompletados.sort((q1, q2) -> {
//            if (q1.getFechaFinalizacion() == null && q2.getFechaFinalizacion() == null) return 0;
//            if (q1.getFechaFinalizacion() == null) return 1;
//            if (q2.getFechaFinalizacion() == null) return -1;
//            return q1.getFechaFinalizacion().compareTo(q2.getFechaFinalizacion());
//        });
//
//        // Calcular puntos progresivos para cada quehacer completado
//        for (Quehacer q : quehaceresCompletados) {
//            if (q.getMiembroHogar() != null) {
//                String nombreMiembro = q.getMiembroHogar().getNombre();
//                int puntosActuales = puntosProgresivos.getOrDefault(nombreMiembro, 0);
//
//                if (q.isCompletado()) {
//                    puntosActuales += 20; // Sumar 20 por completado a tiempo
//                } else {
//                    puntosActuales = Math.max(0, puntosActuales - 10); // Restar 10 por atrasado
//                }
//
//                puntosProgresivos.put(nombreMiembro, puntosActuales);
//
//                // Crear un atributo especial para este quehacer con sus puntos en ese momento
//                q.setPuntosEnEseMomento(puntosActuales);
//                System.out.println("[DEBUG] " + nombreMiembro + " tenía " + puntosActuales + " puntos después de " + q.getNombre());
//            }
//        }
//
//        // Para pendientes, mostrar puntos actuales del miembro
//        for (Quehacer q : quehaceresPendientes) {
//            if (q.getMiembroHogar() != null) {
//                q.setPuntosEnEseMomento(q.getMiembroHogar().getPuntos());
//            }
//        }
//
//        // Recombinar las listas: primero completados (ordenados por fecha), luego pendientes
//        List<Quehacer> listaQuehaceres = new ArrayList<>();
//        listaQuehaceres.addAll(quehaceresCompletados);
//        listaQuehaceres.addAll(quehaceresPendientes);
//
//        // Log detallado de los quehaceres cargados
//        for (Quehacer q : listaQuehaceres) {
//            System.out.println("[DEBUG] - Quehacer: " + q.getNombre() + " | Asignado a: " +
//                (q.getMiembroHogar() != null ? q.getMiembroHogar().getNombre() : "SIN ASIGNAR") +
//                " | Estado: " + (q.isCompletado() ? "Completado" : "Pendiente") +
//                " | Puntos en ese momento: " + q.getPuntosEnEseMomento());
//        }
//
//        request.setAttribute("listaQuehaceres", listaQuehaceres);
//        System.out.println("[DEBUG] Redirigiendo a tablero.jsp");
//        request.getRequestDispatcher("/tablero.jsp").forward(request, response);
//    }
//
//    private void listGestionQuehaceres(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        System.out.println("=== CARGANDO GESTIÓN DE QUEHACERES ===");
//
//        // Finalizar automáticamente las tareas vencidas
//        finalizarTareasVencidas();
//
//        // Cargar lista de quehaceres con miembros
//        List<Quehacer> todosLosQuehaceres = quehacerDAO.findAllWithMiembroHogar();
//        System.out.println("[DEBUG] Cargando " + todosLosQuehaceres.size() + " quehaceres para gestión");
//
//        // Calcular puntos progresivos: ordenar por fecha de finalización y calcular puntos acumulados
//        Map<String, Integer> puntosProgresivos = new HashMap<>();
//
//        // Separar completados y pendientes
//        List<Quehacer> quehaceresCompletados = new ArrayList<>();
//        List<Quehacer> quehaceresPendientes = new ArrayList<>();
//
//        // Variables para estadísticas (movidas desde JSP)
//        int totalTareas = todosLosQuehaceres.size();
//        int tareasCompletadas = 0;
//        int tareasPendientes = 0;
//        int tareasVencidas = 0;
//
//        for (Quehacer q : todosLosQuehaceres) {
//            if (q.isEstadoFinalizado()) {
//                quehaceresCompletados.add(q);
//                tareasCompletadas++;
//            } else {
//                quehaceresPendientes.add(q);
//                tareasPendientes++;
//
//                // Verificar si está vencida
//                if (q.getTiempoLimite() != null && q.getTiempoLimite().isBefore(java.time.LocalDateTime.now())) {
//                    tareasVencidas++;
//                }
//            }
//        }
//
//        // Ordenar los completados por fecha de finalización
//        quehaceresCompletados.sort((q1, q2) -> {
//            if (q1.getFechaFinalizacion() == null && q2.getFechaFinalizacion() == null) return 0;
//            if (q1.getFechaFinalizacion() == null) return 1;
//            if (q2.getFechaFinalizacion() == null) return -1;
//            return q1.getFechaFinalizacion().compareTo(q2.getFechaFinalizacion());
//        });
//
//        // Calcular puntos progresivos para cada quehacer completado
//        for (Quehacer q : quehaceresCompletados) {
//            MiembroHogar miembro = q.getMiembroHogar();
//            if (miembro != null) {
//                String nombreMiembro = miembro.getNombre();
//                int puntosActuales = puntosProgresivos.getOrDefault(nombreMiembro, 0);
//
//                // Usar la misma lógica de puntos que en Incentivo
//                if (q.fueCompletadoATiempo()) {
//                    // Puntos según dificultad
//                    int puntosDificultad = switch (q.getDificultad()) {
//                        case FACIL -> 10;
//                        case MEDIO -> 20;
//                        case DIFICIL -> 30;
//                    };
//                    puntosActuales += puntosDificultad;
//                } else {
//                    puntosActuales = Math.max(0, puntosActuales - 5); // PENALIZACION definida en Incentivo
//                }
//
//                puntosProgresivos.put(nombreMiembro, puntosActuales);
//                q.setPuntosEnEseMomento(puntosActuales);
//            }
//        }
//
//        // Para pendientes, mostrar puntos actuales del miembro
//        for (Quehacer q : quehaceresPendientes) {
//            if (q.getMiembroHogar() != null) {
//                q.setPuntosEnEseMomento(q.getMiembroHogar().getPuntos());
//            }
//        }
//
//        // Recombinar las listas: primero completados (ordenados por fecha), luego pendientes
//        List<Quehacer> listaQuehaceres = new ArrayList<>();
//        listaQuehaceres.addAll(quehaceresCompletados);
//        listaQuehaceres.addAll(quehaceresPendientes);
//
//        // Pasar datos y estadísticas calculadas a la vista (no calcular en JSP)
//        request.setAttribute("listaQuehaceres", listaQuehaceres);
//        request.setAttribute("totalTareas", totalTareas);
//        request.setAttribute("tareasCompletadas", tareasCompletadas);
//        request.setAttribute("tareasPendientes", tareasPendientes);
//        request.setAttribute("tareasVencidas", tareasVencidas);
//
//        request.getRequestDispatcher("/quehaceres/index.jsp").forward(request, response);
//    }
//
//    private void showPendingForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        try {
//            // Finalizar automáticamente las tareas vencidas
//            finalizarTareasVencidas();
//
//            // Cargar lista de miembros
//            List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
//            System.out.println("[DEBUG] Lista de miembros para pendientes: " + listaMiembros);
//
//            // Verificar si se seleccionó un miembro específico
//            String miembroIdStr = request.getParameter("miembroId");
//            List<Quehacer> tareasPendientes = new java.util.ArrayList<>();
//
//            if (miembroIdStr != null && !miembroIdStr.isEmpty()) {
//                try {
//                    Long miembroId = Long.parseLong(miembroIdStr);
//                    MiembroHogar miembroSeleccionado = miembroHogarDAO.findById(miembroId);
//
//                    if (miembroSeleccionado != null) {
//                        // Obtener todas las tareas y filtrar las del miembro que están realmente pendientes
//                        List<Quehacer> todasLasTareas = quehacerDAO.findAll();
//                        LocalDateTime ahora = LocalDateTime.now();
//
//                        for (Quehacer q : todasLasTareas) {
//                            if (q.getMiembroHogar() != null &&
//                                q.getMiembroHogar().getId().equals(miembroId) &&
//                                !q.isCompletado() && !q.isEstadoFinalizado() &&
//                                ahora.isBefore(q.getTiempoLimite())) { // Solo las que no han vencido
//                                tareasPendientes.add(q);
//                            }
//                        }
//                        System.out.println("[DEBUG] Tareas pendientes (no vencidas) para miembro " + miembroSeleccionado.getNombre() + ": " + tareasPendientes);
//                    }
//                } catch (NumberFormatException e) {
//                    System.out.println("[DEBUG] Error al parsear miembroId: " + e.getMessage());
//                }
//            }
//
//            request.setAttribute("listaMiembros", listaMiembros);
//            request.setAttribute("tareasPendientes", tareasPendientes);
//            request.setAttribute("miembroSeleccionado", miembroIdStr);
//            request.getRequestDispatcher("/quehaceres/pending.jsp").forward(request, response);
//
//        } catch (Exception e) {
//            System.out.println("[ERROR] Error en showPendingForm: " + e.getMessage());
//            e.printStackTrace();
//            request.setAttribute("errorMessage", "Error interno del servidor: " + e.getMessage());
//            request.getRequestDispatcher("/quehaceres/pending.jsp").forward(request, response);
//        }
//    }
//
//    private void showCompleteForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        try {
//            // Finalizar automáticamente las tareas vencidas
//            finalizarTareasVencidas();
//
//            // Cargar lista de miembros
//            List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
//            System.out.println("[DEBUG] Lista de miembros para completar: " + listaMiembros);
//
//            // Cargar lista de quehaceres pendientes (no completados, no finalizados, y no vencidos)
//            List<Quehacer> todosLosQuehaceres = quehacerDAO.findAll();
//            List<Quehacer> listaQuehaceres = new java.util.ArrayList<>();
//            java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
//
//            for (Quehacer q : todosLosQuehaceres) {
//                // Solo mostrar quehaceres que están realmente pendientes (no vencidos, no completados, no finalizados)
//                if (!q.isCompletado() && !q.isEstadoFinalizado() && ahora.isBefore(q.getTiempoLimite())) {
//                    listaQuehaceres.add(q);
//                }
//            }
//            System.out.println("[DEBUG] Lista de quehaceres pendientes (solo no vencidos): " + listaQuehaceres);
//
//            request.setAttribute("listaMiembros", listaMiembros);
//            request.setAttribute("listaQuehaceres", listaQuehaceres);
//            request.getRequestDispatcher("/quehaceres/complete.jsp").forward(request, response);
//
//        } catch (Exception e) {
//            System.out.println("[ERROR] Error en showCompleteForm: " + e.getMessage());
//            e.printStackTrace();
//            request.setAttribute("errorMessage", "Error interno del servidor: " + e.getMessage());
//            request.getRequestDispatcher("/quehaceres/complete.jsp").forward(request, response);
//        }
//    }
//
//    private void markComplete(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String quehacerIdStr = request.getParameter("quehacerId");
//        String fechaFinalizacionStr = request.getParameter("fechaFinalizacion");
//
//        System.out.println("[DEBUG] Marcando quehacer como completado:");
//        System.out.println("[DEBUG] - Quehacer ID: " + quehacerIdStr);
//        System.out.println("[DEBUG] - Fecha finalización: " + fechaFinalizacionStr);
//
//        try {
//            Long id = Long.parseLong(quehacerIdStr);
//            Quehacer quehacer = quehacerDAO.findById(id);
//
//            if (quehacer == null) {
//                request.getSession().setAttribute("errorMessage", "Quehacer no encontrado");
//                response.sendRedirect(request.getContextPath() + "/quehaceres?action=complete");
//                return;
//            }
//
//            // VALIDACIÓN: Solo se pueden completar tareas que están pendientes
//            if (quehacer.isCompletado()) { // Usar método del diagrama UML
//                request.getSession().setAttribute("errorMessage", "Esta tarea ya está completada");
//                response.sendRedirect(request.getContextPath() + "/quehaceres?action=complete");
//                return;
//            }
//
//            if (quehacer.isEstadoFinalizado()) {
//                request.getSession().setAttribute("errorMessage", "Esta tarea ya está finalizada (vencida)");
//                response.sendRedirect(request.getContextPath() + "/quehaceres?action=complete");
//                return;
//            }
//
//            LocalDateTime ahora = LocalDateTime.now();
//            if (ahora.isAfter(quehacer.getTiempoLimite())) {
//                request.getSession().setAttribute("errorMessage", "Esta tarea ya venció. No se puede marcar como completada.");
//                response.sendRedirect(request.getContextPath() + "/quehaceres?action=complete");
//                return;
//            }
//
//            LocalDateTime fechaFinalizacion = LocalDateTime.parse(fechaFinalizacionStr);
//
//            // Preparar información previa para detectar ascenso/emblema
//            MiembroHogar miembro = quehacer.getMiembroHogar();
//            int puntosAntes = miembro != null ? miembro.getPuntos() : 0;
//            Liga ligaAntes = miembro != null ? miembro.getLiga() : null;
//
//            // Marcar como completado y aplicar incentivo usando la lógica unificada
//            quehacer.setFechaFinalizacion(fechaFinalizacion);
//            quehacer.marcarCompletado();
//            Incentivo.aplicarIncentivo(quehacer.getMiembroHogar(), quehacer);
//
//            // Persistir el quehacer (el miembro ya se persiste dentro del servicio de incentivos)
//            quehacerDAO.update(quehacer);
//
//            // Información posterior para construir la notificación
//            int puntosActuales = miembro != null ? miembro.getPuntos() : 0;
//            Liga ligaDespues = miembro != null ? miembro.getLiga() : null;
//
//            // Determinar puntos cambiados según dificultad
//            int puntosCambio = 0;
//            if (quehacer.fueCompletadoATiempo()) {
//                puntosCambio = switch (quehacer.getDificultad()) {
//                    case FACIL -> 10;
//                    case MEDIO -> 20;
//                    case DIFICIL -> 30;
//                };
//            } else {
//                puntosCambio = -switch (quehacer.getDificultad()) {
//                    case FACIL -> 10;
//                    case MEDIO -> 20;
//                    case DIFICIL -> 30;
//                };
//            }
//
//            // Intentar asignar emblemas localmente y construir mensaje de notificación
//            EmblemaService emblemaService = new EmblemaService();
//            String emblemaAsignado = null;
//            boolean asignado = false;
//
//            // Primera liga (detectada por puntos antes == 0)
//            if ((ligaAntes == null || puntosAntes == 0) && ligaDespues != null) {
//                asignado = emblemaService.asignarEmblemaAprendiz(miembro.getId());
//                if (asignado) emblemaAsignado = "APRENDIZ";
//            }
//
//            // Ascenso entre ligas
//            if (!asignado && ligaAntes != null && ligaDespues != null && ligaDespues.ordinal() > ligaAntes.ordinal()) {
//                asignado = emblemaService.asignarEmblemaAscenso(miembro.getId(), ligaAntes.name(), ligaDespues.name());
//                if (asignado) emblemaAsignado = "ASCENSO_" + ligaAntes.name() + "_TO_" + ligaDespues.name();
//            }
//
//            // Construir mensaje final
//            String nombreMiembro = miembro != null ? miembro.getNombre() : "desconocido";
//            String mensajeBase = "¡Quehacer completado! " + nombreMiembro + " ";
//            mensajeBase += (puntosCambio >= 0 ? "ganó +" + puntosCambio : "perdió " + Math.abs(puntosCambio)) + " puntos. ";
//            mensajeBase += "Total actual: " + puntosActuales + " puntos.";
//            if (emblemaAsignado != null) {
//                mensajeBase += " Además obtuvo el emblema: '" + emblemaAsignado + "'.";
//            } else if (ligaAntes != null && ligaDespues != null && ligaDespues.ordinal() > ligaAntes.ordinal()) {
//                // Si ascendió pero no se asignó emblema (por ejemplo ya lo tenía), mostrar ascenso
//                mensajeBase += " ¡Felicidades! Has ascendido de " + ligaAntes.name() + " a " + ligaDespues.name() + ".";
//            }
//
//            request.getSession().setAttribute("successMessage", mensajeBase);
//            System.out.println("[DEBUG] Quehacer actualizado exitosamente: " + mensajeBase);
//
//        } catch (Exception e) {
//            System.out.println("[DEBUG] Error al marcar quehacer como completado: " + e.getMessage());
//            e.printStackTrace();
//            request.getSession().setAttribute("errorMessage", "Error al marcar el quehacer como completado: " + e.getMessage());
//        }
//
//        response.sendRedirect(request.getContextPath() + "/quehaceres?action=complete");
//    }
//
//
//
//    private void finalizarTareasVencidas() {
//        try {
//            List<Quehacer> todosLosQuehaceres = quehacerDAO.findAll();
//            LocalDateTime ahora = LocalDateTime.now();
//
//            for (Quehacer quehacer : todosLosQuehaceres) {
//                // Si el quehacer no está completado, no está finalizado, y ya se venció
//                if (!quehacer.isCompletado() && !quehacer.isEstadoFinalizado() &&
//                    quehacer.estaVencido()) {
//
//                    System.out.println("[DEBUG] Finalizando tarea vencida: " + quehacer.getNombre());
//
//                    // Marcar como finalizado (usar EstadoQuehacer y flag vencido)
//                    quehacer.setEstado(EstadoQuehacer.VENCIDO);
//                    quehacer.setVencido(true);
//                    quehacer.setFechaFinalizacion(ahora);
//
//                    // Aplicar incentivo (será penalización por estar vencida)
//                    MiembroHogar miembro = quehacer.getMiembroHogar();
//                    if (miembro != null) {
//                        Incentivo.aplicarIncentivo(miembro, quehacer);
//                        miembroHogarDAO.update(miembro);
//                    }
//
//                    quehacerDAO.update(quehacer);
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("[ERROR] Error al finalizar tareas vencidas: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    public void testFindAllMiembros() {
//        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
//        logger.info("Resultados de findAll: " + listaMiembros);
//    }
//
//}
//
package servlet;
import model.*;
import dao.QuehacerDAO;
import dao.MiembroHogarDAO;
import service.HogarService;
import service.EmblemaService; // Importar el nuevo servicio

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
    private EmblemaService emblemaService; // <<<--- 1. DECLARACIÓN DEL SERVICIO DE EMBLEMAS

    private static final Logger logger = Logger.getLogger(QuehacerServlet.class.getName());

    @Override
    public void init() {
        quehacerDAO = new QuehacerDAO();
        miembroHogarDAO = new MiembroHogarDAO();
        hogarService = new HogarService();
        emblemaService = EmblemaService.getInstancia(); // <<<--- 2. INICIALIZACIÓN DEL SERVICIO
        testFindAllMiembros();
    }

    // ... (El resto de los métodos doGet y los métodos privados que no cambian permanecen igual)
    // ... (doGet, doPost, showNewForm, insertQuehacer, deleteQuehacer, listQuehaceres, etc.)
    // El único método que cambia es markComplete.

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
        // 1. Cargar todos los miembros para poder obtener sus emblemas
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();

        // 2. Crear un mapa para pasar los emblemas al JSP
        Map<Long, Set<String>> emblemasPorMiembro = new HashMap<>();

        // 3. Poblar el mapa con los emblemas de cada miembro
        for (MiembroHogar miembro : listaMiembros) {
            // Usamos el servicio para obtener los emblemas del miembro actual
            Set<String> emblemas = emblemaService.obtenerEmblemas(miembro.getId());
            emblemasPorMiembro.put(miembro.getId(), emblemas);
        }

        // 4. Añadir el mapa de emblemas al request para que el JSP pueda acceder a él
        request.setAttribute("emblemasPorMiembro", emblemasPorMiembro);
        // <<<--- FIN DE LA MODIFICACIÓN ---


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

            String mensajeEmblema = ""; // Mensaje adicional para notificar sobre emblemas

            if (miembro != null) {
                // <<<--- 3. LÓGICA DE EMBLEMAS ---
                // Guardar la liga ANTES de aplicar el incentivo
                Liga ligaAnterior = miembro.getLiga();

                System.out.println("[DEBUG] Aplicando incentivo para miembro: " + miembro.getNombre());
                Incentivo.aplicarIncentivo(miembro, quehacer);

                // Obtener la liga DESPUÉS de aplicar el incentivo
                Liga ligaNueva = miembro.getLiga();

                // Verificar si se ganó un emblema
                if (ligaNueva != null) {
                    // Ganó su primera liga: Emblema de Aprendiz
                    if (emblemaService.asignarEmblemaAprendiz(miembro)) {
                        mensajeEmblema = " ¡Y ha ganado el emblema de APRENDIZ!";
                        logger.info("Miembro " + miembro.getNombre() + " ha ganado el emblema APRENDIZ.");
                    }
                } else if (ligaAnterior != null && !ligaAnterior.equals(ligaNueva)) {
                    // Fue promovido: Emblema de Ascenso
                    if (emblemaService.asignarEmblemaAscenso(miembro.getId(), String.valueOf(ligaAnterior), String.valueOf(ligaNueva))) {
                        mensajeEmblema = " ¡Y ha ascendido a la liga " + ligaNueva + ", ganando un nuevo emblema!";
                        logger.info("Miembro " + miembro.getNombre() + " ascendió de " + ligaAnterior + " a " + ligaNueva);
                    }
                }
                // --- FIN LÓGICA DE EMBLEMAS --->>>

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

            // Construir el mensaje final, incluyendo la notificación del emblema
            request.getSession().setAttribute("successMessage",
                    "¡Quehacer completado! " + nombreMiembro + " " + (puntosGanados >= 0 ? "ganó " : "perdió ") +
                            Math.abs(puntosGanados) + " puntos. Total actual: " + puntosActuales + " puntos." + mensajeEmblema);

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