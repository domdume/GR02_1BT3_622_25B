package servlet;

import dao.MiembroHogarDAO;
import dao.QuehacerDAO;
import gui.InterfazDelJefe;
import gui.InterfazDelMiembroFamilia;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Dificultad;
import model.JefeDelHogar;
import model.MiembroHogar;
import model.Quehacer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

@WebServlet(name = "QuehacerServlet", value = "/quehaceres")
public class QuehacerServlet extends HttpServlet {
    private QuehacerDAO quehacerDAO;
    private MiembroHogarDAO miembroHogarDAO;

    private static final Logger logger = Logger.getLogger(QuehacerServlet.class.getName());

    @Override
    public void init() {
        quehacerDAO = new QuehacerDAO();
        miembroHogarDAO = new MiembroHogarDAO();

        testFindAllMiembros();
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
            case "interfazJefe":
                mostrarInterfazJefe(request, response);
                break;
            case "interfazMiembro":
                mostrarInterfazMiembro(request, response);
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
            // Ejecutar funcionalidad de InterfazDelJefe según diagrama UML
            System.out.println("=== EJECUTANDO LÓGICA DEL DIAGRAMA UML: InterfazDelJefe ===");
            try {
                JefeDelHogar jefe = new JefeDelHogar("Jefe del Hogar", 45);
                new InterfazDelJefe(jefe); // Ejecutar lógica UML
                request.setAttribute("mensaje", "🏗️ Lógica del Diagrama UML ejecutada: InterfazDelJefe.registrarUnQuehacer()");
            } catch (Exception e) {
                System.out.println("[DEBUG] Error en lógica UML: " + e.getMessage());
                request.setAttribute("mensaje", "⚠️ Lógica UML ejecutada con advertencias");
            }
            
            List<MiembroHogar> listaMiembros = miembroHogarDAO.obtenerTodos(); // Cargar miembros desde la BD usando nuevo método
            System.out.println("[DEBUG] Lista de miembros recuperada con obtenerTodos(): " + listaMiembros);
            logger.info("Lista de miembros recuperada: " + listaMiembros);

            // Cargar también la lista de quehaceres existentes para mostrar en la tabla
            List<Quehacer> todosLosQuehaceres = quehacerDAO.findAllWithMiembroHogar();
            System.out.println("[DEBUG] Lista de quehaceres recuperada: " + todosLosQuehaceres.size() + " elementos");
            
            // Aplicar lógica de puntos progresivos
            Map<String, Integer> puntosProgresivos = new HashMap<>();
            List<Quehacer> quehaceresCompletados = new ArrayList<>();
            List<Quehacer> quehaceresPendientes = new ArrayList<>();
            
            for (Quehacer q : todosLosQuehaceres) {
                if (q.isEstadoFinalizado()) {
                    quehaceresCompletados.add(q);
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
                    
                    if (q.estaCompletado()) {
                        puntosActuales += 20;
                    } else {
                        puntosActuales = Math.max(0, puntosActuales - 10);
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
            System.out.println("[ERROR] Error en showNewForm: " + e.getMessage());
            e.printStackTrace();
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
            Long miembroId = Long.parseLong(miembroIdStr);
            MiembroHogar miembro = miembroHogarDAO.findById(miembroId);
            System.out.println("[DEBUG] - Miembro encontrado: " + (miembro != null ? miembro.getNombre() : "null"));
            
            LocalDateTime tiempoLimite = LocalDateTime.parse(tiempoLimiteStr);
            
            // Usar dificultad del formulario o MEDIO por defecto
            Dificultad dificultad = Dificultad.MEDIO; // Valor por defecto
            if (dificultadStr != null && !dificultadStr.isEmpty()) {
                try {
                    dificultad = Dificultad.valueOf(dificultadStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("[DEBUG] Dificultad inválida, usando MEDIO por defecto");
                }
            }

            // Usar el nuevo constructor con dificultad según el diagrama UML
            Quehacer nuevoQuehacer = new Quehacer(nombre, tiempoLimite, dificultad);
            nuevoQuehacer.setMiembroHogar(miembro);

            quehacerDAO.create(nuevoQuehacer);
            System.out.println("[DEBUG] Quehacer creado exitosamente");
            request.getSession().setAttribute("successMessage", "Quehacer agregado correctamente.");
        } catch (Exception e) {
            System.out.println("[DEBUG] Error al crear quehacer: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error al agregar el quehacer: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/quehaceres?action=listGestion");
    }

    private void deleteQuehacer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        quehacerDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/quehaceres?action=listGestion");
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
        
        // Separar completados y pendientes
        List<Quehacer> quehaceresCompletados = new ArrayList<>();
        List<Quehacer> quehaceresPendientes = new ArrayList<>();
        
        for (Quehacer q : todosLosQuehaceres) {
            if (q.isEstadoFinalizado()) {
                quehaceresCompletados.add(q);
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
                
                if (q.estaCompletado()) {
                    puntosActuales += 20; // Sumar 20 por completado a tiempo
                } else {
                    puntosActuales = Math.max(0, puntosActuales - 10); // Restar 10 por atrasado
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
        
        // Recombinar las listas: primero completados (ordenados por fecha), luego pendientes
        List<Quehacer> listaQuehaceres = new ArrayList<>();
        listaQuehaceres.addAll(quehaceresCompletados);
        listaQuehaceres.addAll(quehaceresPendientes);
        
        // Log detallado de los quehaceres cargados
        for (Quehacer q : listaQuehaceres) {
            System.out.println("[DEBUG] - Quehacer: " + q.getNombre() + " | Asignado a: " + 
                (q.getMiembroHogar() != null ? q.getMiembroHogar().getNombre() : "SIN ASIGNAR") + 
                " | Estado: " + (q.estaCompletado() ? "Completado" : "Pendiente") + 
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
        
        // Separar completados y pendientes
        List<Quehacer> quehaceresCompletados = new ArrayList<>();
        List<Quehacer> quehaceresPendientes = new ArrayList<>();
        
        for (Quehacer q : todosLosQuehaceres) {
            if (q.isEstadoFinalizado()) {
                quehaceresCompletados.add(q);
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
                
                if (q.estaCompletado()) {
                    puntosActuales += 20; // Sumar 20 por completado a tiempo
                } else {
                    puntosActuales = Math.max(0, puntosActuales - 10); // Restar 10 por atrasado
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
        
        // Recombinar las listas: primero completados (ordenados por fecha), luego pendientes
        List<Quehacer> listaQuehaceres = new ArrayList<>();
        listaQuehaceres.addAll(quehaceresCompletados);
        listaQuehaceres.addAll(quehaceresPendientes);
        
        request.setAttribute("listaQuehaceres", listaQuehaceres);
        request.getRequestDispatcher("/quehaceres/index.jsp").forward(request, response);
    }

    private void showPendingForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Ejecutar funcionalidad de InterfazDelMiembroFamilia según diagrama UML
            System.out.println("=== EJECUTANDO LÓGICA DEL DIAGRAMA UML: InterfazDelMiembroFamilia.revisarQuehaceres() ===");
            
            // Primero, finalizar automáticamente las tareas vencidas
            finalizarTareasVencidas();
            
            // Cargar lista de miembros
            List<MiembroHogar> listaMiembros = miembroHogarDAO.obtenerTodos();
            System.out.println("[DEBUG] Lista de miembros para pendientes: " + listaMiembros);
            
            // Ejecutar lógica del diagrama UML para todos los miembros (con manejo de errores)
            try {
                for (MiembroHogar miembro : listaMiembros) {
                    InterfazDelMiembroFamilia interfazMiembro = new InterfazDelMiembroFamilia(miembro);
                    interfazMiembro.revisarQuehaceres(); // Ejecutar método del diagrama UML
                }
                request.setAttribute("mensaje", "🏗️ Lógica del Diagrama UML ejecutada: InterfazDelMiembroFamilia.revisarQuehaceres() para todos los miembros");
            } catch (Exception e) {
                System.out.println("[DEBUG] Error en lógica UML: " + e.getMessage());
                request.setAttribute("mensaje", "⚠️ Lógica UML ejecutada con advertencias");
            }
            
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
                                !q.estaCompletado() && !q.isEstadoFinalizado() &&
                                ahora.isBefore(q.getTiempoLimite())) { // Solo las que no han vencido
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
            System.out.println("[ERROR] Error en showPendingForm: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error interno del servidor: " + e.getMessage());
            request.getRequestDispatcher("/quehaceres/pending.jsp").forward(request, response);
        }
    }

    private void showCompleteForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Ejecutar funcionalidad de InterfazDelMiembroFamilia según diagrama UML
            System.out.println("=== EJECUTANDO LÓGICA DEL DIAGRAMA UML: InterfazDelMiembroFamilia ===");
            
            // Primero, finalizar automáticamente las tareas vencidas
            finalizarTareasVencidas();
            
            // Cargar lista de miembros
            List<MiembroHogar> listaMiembros = miembroHogarDAO.obtenerTodos();
            System.out.println("[DEBUG] Lista de miembros para completar: " + listaMiembros);
            
            // Ejecutar lógica del diagrama UML para el primer miembro disponible (si existe)
            if (!listaMiembros.isEmpty()) {
                try {
                    MiembroHogar primerMiembro = listaMiembros.get(0);
                    InterfazDelMiembroFamilia interfazMiembro = new InterfazDelMiembroFamilia(primerMiembro);
                    interfazMiembro.revisarQuehaceres(); // Ejecutar método del diagrama UML
                    request.setAttribute("mensaje", "🏗️ Lógica del Diagrama UML ejecutada: InterfazDelMiembroFamilia.completarUnQuehacer() para " + primerMiembro.getNombre());
                } catch (Exception e) {
                    System.out.println("[DEBUG] Error en lógica UML: " + e.getMessage());
                    request.setAttribute("mensaje", "⚠️ Lógica UML ejecutada con advertencias");
                }
            }
            
            // Cargar lista de quehaceres pendientes (no completados, no finalizados, y no vencidos)
            List<Quehacer> todosLosQuehaceres = quehacerDAO.findAll();
            List<Quehacer> listaQuehaceres = new java.util.ArrayList<>();
            java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
            
            for (Quehacer q : todosLosQuehaceres) {
                // Solo mostrar quehaceres que están realmente pendientes (no vencidos, no completados, no finalizados)
                if (!q.estaCompletado() && !q.isEstadoFinalizado() && ahora.isBefore(q.getTiempoLimite())) {
                    listaQuehaceres.add(q);
                }
            }
            System.out.println("[DEBUG] Lista de quehaceres pendientes (solo no vencidos): " + listaQuehaceres);
            
            request.setAttribute("listaMiembros", listaMiembros);
            request.setAttribute("listaQuehaceres", listaQuehaceres);
            request.getRequestDispatcher("/quehaceres/complete.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.out.println("[ERROR] Error en showCompleteForm: " + e.getMessage());
            e.printStackTrace();
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

            // VALIDACIÓN: Solo se pueden completar tareas que están pendientes
            if (quehacer.estaCompletado()) { // Usar método del diagrama UML
                request.getSession().setAttribute("errorMessage", "Esta tarea ya está completada");
                response.sendRedirect(request.getContextPath() + "/quehaceres?action=complete");
                return;
            }
            
            if (quehacer.isEstadoFinalizado()) {
                request.getSession().setAttribute("errorMessage", "Esta tarea ya está finalizada (vencida)");
                response.sendRedirect(request.getContextPath() + "/quehaceres?action=complete");
                return;
            }
            
            LocalDateTime ahora = LocalDateTime.now();
            if (ahora.isAfter(quehacer.getTiempoLimite())) {
                request.getSession().setAttribute("errorMessage", "Esta tarea ya venció. No se puede marcar como completada.");
                response.sendRedirect(request.getContextPath() + "/quehaceres?action=complete");
                return;
            }

            LocalDateTime fechaFinalizacion = LocalDateTime.parse(fechaFinalizacionStr);
            quehacer.setFechaFinalizacion(fechaFinalizacion);

            List<String> listaRecompensas = List.of("5 puntos", "10 puntos", "Medalla de honor");

            // Usar el método marcarCompletado del diagrama UML
            quehacer.marcarCompletado();
            quehacer.setRecompensa(listaRecompensas.get(new Random().nextInt(listaRecompensas.size())));
            
            // Sumar puntos por completar a tiempo
            MiembroHogar miembro = quehacer.getMiembroHogar();
            if (miembro != null) {
                int puntosActuales = miembro.getPuntos();
                int recompensaPuntos = 20; // Sumar 20 puntos por completar a tiempo
                miembro.setPuntos(puntosActuales + recompensaPuntos);
                miembroHogarDAO.update(miembro);
                System.out.println("[DEBUG] Puntos sumados a " + miembro.getNombre() + ": +" + recompensaPuntos + " (Total: " + miembro.getPuntos() + ")");
            }
            
            System.out.println("[DEBUG] Quehacer completado A TIEMPO - recompensa asignada");

            quehacerDAO.update(quehacer);
            String nombreMiembro = miembro != null ? miembro.getNombre() : "desconocido";
            int puntosActuales = miembro != null ? miembro.getPuntos() : 0;
            request.getSession().setAttribute("successMessage", 
                "¡Quehacer completado! " + nombreMiembro + " ganó +20 puntos. Total actual: " + puntosActuales + " puntos.");
            System.out.println("[DEBUG] Quehacer actualizado exitosamente");
            
        } catch (Exception e) {
            System.out.println("[DEBUG] Error al marcar quehacer como completado: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Error al marcar el quehacer como completado: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/quehaceres?action=complete");
    }

    private void finalizarTareasVencidas() {
        try {
            List<Quehacer> todosLosQuehaceres = quehacerDAO.findAll();
            LocalDateTime ahora = LocalDateTime.now();
            
            for (Quehacer quehacer : todosLosQuehaceres) {
                // Si el quehacer no está completado, no está finalizado, y ya se venció
                if (!quehacer.estaCompletado() && !quehacer.isEstadoFinalizado() && 
                    ahora.isAfter(quehacer.getTiempoLimite())) {
                    
                    System.out.println("[DEBUG] Finalizando tarea vencida: " + quehacer.getNombre());
                    
                    // Marcar como finalizado (vencido)
                    quehacer.setEstadoFinalizado(true);
                    quehacer.setEstadoCompletado(false);
                    quehacer.setFechaFinalizacion(ahora);
                    quehacer.setPenalizacion("Tarea no completada a tiempo");
                    
                    // Restar puntos por no completar a tiempo
                    MiembroHogar miembro = quehacer.getMiembroHogar();
                    if (miembro != null) {
                        int puntosActuales = miembro.getPuntos();
                        int penalizacionPuntos = 10; // Restar 10 puntos por no completar
                        miembro.setPuntos(Math.max(0, puntosActuales - penalizacionPuntos));
                        miembroHogarDAO.update(miembro);
                        System.out.println("[DEBUG] Penalización aplicada a " + miembro.getNombre() + 
                                         ": -" + penalizacionPuntos + " puntos (Total: " + miembro.getPuntos() + ")");
                    }
                    
                    // Actualizar el quehacer en la base de datos
                    quehacerDAO.update(quehacer);
                }
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Error al finalizar tareas vencidas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void testFindAllMiembros() {
        List<MiembroHogar> listaMiembros = miembroHogarDAO.findAll();
        logger.info("Resultados de findAll: " + listaMiembros);
    }

    // Métodos para integrar las interfaces GUI según el diagrama UML
    private void mostrarInterfazJefe(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[DEBUG] Mostrando Interfaz del Jefe del Hogar");
        
        // Crear un JefeDelHogar de ejemplo (en un sistema real, esto vendría de la sesión)
        JefeDelHogar jefe = new JefeDelHogar("Jefe del Hogar", 45);
        
        // Usar la interfaz GUI según el diagrama UML
        InterfazDelJefe interfazJefe = new InterfazDelJefe(jefe);
        
        // Ejecutar funcionalidad de ejemplo
        interfazJefe.registrarUnQuehacer();
        interfazJefe.registrarUnMiembro();
        
        // Cargar datos actualizados para mostrar
        List<Quehacer> listaQuehaceres = quehacerDAO.findAllWithMiembroHogar();
        List<MiembroHogar> listaMiembros = miembroHogarDAO.obtenerTodos();
        
        request.setAttribute("listaQuehaceres", listaQuehaceres);
        request.setAttribute("listaMiembros", listaMiembros);
        request.setAttribute("mensaje", "Interfaz del Jefe del Hogar ejecutada correctamente");
        
        request.getRequestDispatcher("/quehaceres/index.jsp").forward(request, response);
    }
    
    private void mostrarInterfazMiembro(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[DEBUG] Mostrando Interfaz del Miembro de la Familia");
        
        String miembroIdStr = request.getParameter("miembroId");
        
        if (miembroIdStr != null && !miembroIdStr.isEmpty()) {
            try {
                Long miembroId = Long.parseLong(miembroIdStr);
                MiembroHogar miembro = miembroHogarDAO.findById(miembroId);
                
                if (miembro != null) {
                    // Usar la interfaz GUI según el diagrama UML
                    InterfazDelMiembroFamilia interfazMiembro = new InterfazDelMiembroFamilia(miembro);
                    
                    // Ejecutar funcionalidades
                    interfazMiembro.revisarQuehaceres();
                    interfazMiembro.completarUnQuehacer();
                    
                    request.setAttribute("mensaje", "Interfaz del Miembro ejecutada para: " + miembro.getNombre());
                } else {
                    request.setAttribute("errorMessage", "Miembro no encontrado");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID de miembro inválido");
            }
        } else {
            request.setAttribute("errorMessage", "Debe seleccionar un miembro");
        }
        
        // Cargar datos para mostrar
        List<MiembroHogar> listaMiembros = miembroHogarDAO.obtenerTodos();
        request.setAttribute("listaMiembros", listaMiembros);
        
        request.getRequestDispatcher("/miembros/index.jsp").forward(request, response);
    }
}

