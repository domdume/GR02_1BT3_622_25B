package util;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador centralizado de rutas para la aplicación MVC
 * Mantiene las convenciones de URL y navegación consistentes
 * 
 * Patrón: /{modulo}?action={accion}&{parametros}
 */
public class RouteController {
    
    // Módulos principales del sistema
    public static final String MODULE_HOME = "";
    public static final String MODULE_MIEMBROS = "miembros";
    public static final String MODULE_QUEHACERES = "quehaceres";
    public static final String MODULE_INCENTIVOS = "incentivos";
    
    // Acciones estándar (comunes a todos los módulos)
    public static final String ACTION_LIST = "list";
    public static final String ACTION_NEW = "new";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_INSERT = "insert";
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_UPDATE = "update";
    
    // Acciones específicas por módulo
    public static final class MiembrosActions {
        public static final String TASKS = "tasks";
    }
    
    public static final class QuehaceresActions {
        public static final String PENDING = "pending";
        public static final String COMPLETE = "complete";
        public static final String MARK_COMPLETE = "markComplete";
        public static final String LIST_GESTION = "listGestion";
    }
    
    public static final class IncentivosActions {
        public static final String HISTORY = "history";
        public static final String STATISTICS = "statistics";
        public static final String BY_MIEMBRO = "byMiembro";
        public static final String TOGGLE = "toggle";
    }
    
    // Vistas JSP por módulo
    private static final Map<String, Map<String, String>> moduleViews = new HashMap<>();
    
    static {
        // Vistas del módulo HOME
        Map<String, String> homeViews = new HashMap<>();
        homeViews.put(ACTION_LIST, "/dashboard.jsp");
        moduleViews.put(MODULE_HOME, homeViews);
        
        // Vistas del módulo MIEMBROS
        Map<String, String> miembrosViews = new HashMap<>();
        miembrosViews.put(ACTION_LIST, "/miembros/index.jsp");
        miembrosViews.put(ACTION_NEW, "/miembros/form.jsp");
        miembrosViews.put(ACTION_EDIT, "/miembros/form.jsp");
        miembrosViews.put(MiembrosActions.TASKS, "/miembros/tasks.jsp");
        moduleViews.put(MODULE_MIEMBROS, miembrosViews);
        
        // Vistas del módulo QUEHACERES
        Map<String, String> quehaceresViews = new HashMap<>();
        quehaceresViews.put(ACTION_LIST, "/quehaceres/index.jsp");
        quehaceresViews.put(ACTION_NEW, "/quehaceres/form.jsp");
        quehaceresViews.put(ACTION_EDIT, "/quehaceres/form.jsp");
        quehaceresViews.put(QuehaceresActions.PENDING, "/quehaceres/pending.jsp");
        quehaceresViews.put(QuehaceresActions.COMPLETE, "/quehaceres/complete.jsp");
        quehaceresViews.put(QuehaceresActions.LIST_GESTION, "/quehaceres/index.jsp");
        moduleViews.put(MODULE_QUEHACERES, quehaceresViews);
        
        // Vistas del módulo INCENTIVOS
        Map<String, String> incentivosViews = new HashMap<>();
        incentivosViews.put(ACTION_LIST, "/incentivos/index.jsp");
        incentivosViews.put(ACTION_NEW, "/incentivos/form.jsp");
        incentivosViews.put(ACTION_EDIT, "/incentivos/form.jsp");
        incentivosViews.put(IncentivosActions.HISTORY, "/incentivos/history.jsp");
        incentivosViews.put(IncentivosActions.STATISTICS, "/incentivos/statistics.jsp");
        incentivosViews.put(IncentivosActions.BY_MIEMBRO, "/incentivos/byMiembro.jsp");
        moduleViews.put(MODULE_INCENTIVOS, incentivosViews);
    }
    
    /**
     * Construye una URL siguiendo las convenciones del sistema
     * 
     * @param contextPath El context path de la aplicación
     * @param module El módulo (miembros, quehaceres, incentivos)
     * @param action La acción a realizar
     * @param parameters Parámetros adicionales (opcional)
     * @return URL completa siguiendo convenciones
     */
    public static String buildURL(String contextPath, String module, String action, Map<String, String> parameters) {
        StringBuilder url = new StringBuilder();
        url.append(contextPath);
        
        if (!module.isEmpty()) {
            url.append("/").append(module);
        }
        
        boolean hasParams = false;
        
        // Agregar acción si no es la default (list)
        if (action != null && !ACTION_LIST.equals(action)) {
            url.append("?action=").append(action);
            hasParams = true;
        }
        
        // Agregar parámetros adicionales
        if (parameters != null) {
            for (Map.Entry<String, String> param : parameters.entrySet()) {
                if (hasParams) {
                    url.append("&");
                } else {
                    url.append("?");
                    hasParams = true;
                }
                url.append(param.getKey()).append("=").append(param.getValue());
            }
        }
        
        return url.toString();
    }
    
    /**
     * Construye una URL simple sin parámetros adicionales
     */
    public static String buildURL(String contextPath, String module, String action) {
        return buildURL(contextPath, module, action, null);
    }
    
    /**
     * Construye una URL para listar elementos de un módulo
     */
    public static String buildListURL(String contextPath, String module) {
        return buildURL(contextPath, module, ACTION_LIST);
    }
    
    /**
     * Construye una URL para crear nuevo elemento
     */
    public static String buildNewURL(String contextPath, String module) {
        return buildURL(contextPath, module, ACTION_NEW);
    }
    
    /**
     * Construye una URL para editar un elemento
     */
    public static String buildEditURL(String contextPath, String module, String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        return buildURL(contextPath, module, ACTION_EDIT, params);
    }
    
    /**
     * Construye una URL para eliminar un elemento
     */
    public static String buildDeleteURL(String contextPath, String module, String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        return buildURL(contextPath, module, ACTION_DELETE, params);
    }
    
    /**
     * Obtiene la vista JSP correspondiente a un módulo y acción
     */
    public static String getView(String module, String action) {
        Map<String, String> views = moduleViews.get(module);
        if (views != null) {
            String view = views.get(action);
            if (view != null) {
                return view;
            }
        }
        
        // Vista por defecto si no se encuentra
        return "/" + module + "/index.jsp";
    }
    
    /**
     * Valida si una acción es válida para un módulo
     */
    public static boolean isValidAction(String module, String action) {
        Map<String, String> views = moduleViews.get(module);
        return views != null && views.containsKey(action);
    }
    
    /**
     * Obtiene la acción por defecto para cualquier módulo
     */
    public static String getDefaultAction() {
        return ACTION_LIST;
    }
    
    /**
     * URLs específicas para navegación común
     */
    public static class CommonRoutes {
        
        public static String home(String contextPath) {
            return contextPath + "/home";
        }
        
        public static String miembros(String contextPath) {
            return buildListURL(contextPath, MODULE_MIEMBROS);
        }
        
        public static String quehaceres(String contextPath) {
            return buildListURL(contextPath, MODULE_QUEHACERES);
        }
        
        public static String incentivos(String contextPath) {
            return buildListURL(contextPath, MODULE_INCENTIVOS);
        }
        
        public static String newMiembro(String contextPath) {
            return buildNewURL(contextPath, MODULE_MIEMBROS);
        }
        
        public static String newQuehacer(String contextPath) {
            return buildNewURL(contextPath, MODULE_QUEHACERES);
        }
        
        public static String newIncentivo(String contextPath) {
            return buildNewURL(contextPath, MODULE_INCENTIVOS);
        }
        
        public static String pendingQuehaceres(String contextPath) {
            return buildURL(contextPath, MODULE_QUEHACERES, QuehaceresActions.PENDING);
        }
        
        public static String completeQuehacer(String contextPath) {
            return buildURL(contextPath, MODULE_QUEHACERES, QuehaceresActions.COMPLETE);
        }
        
        public static String incentivosHistory(String contextPath) {
            return buildURL(contextPath, MODULE_INCENTIVOS, IncentivosActions.HISTORY);
        }
    }
    
    /**
     * Utilidades para breadcrumbs
     */
    public static class Breadcrumbs {
        
        public static String getModuleName(String module) {
            switch (module) {
                case MODULE_MIEMBROS: return "Miembros";
                case MODULE_QUEHACERES: return "Quehaceres";
                case MODULE_INCENTIVOS: return "Incentivos";
                default: return "Dashboard";
            }
        }
        
        public static String getActionName(String action) {
            switch (action) {
                case ACTION_NEW: return "Crear";
                case ACTION_EDIT: return "Editar";
                case QuehaceresActions.PENDING: return "Pendientes";
                case QuehaceresActions.COMPLETE: return "Completar";
                case IncentivosActions.HISTORY: return "Historial";
                case IncentivosActions.STATISTICS: return "Estadísticas";
                case MiembrosActions.TASKS: return "Tareas";
                default: return "";
            }
        }
    }
}