# 🗺️ Convención de Rutas - Sistema de Gestión de Quehaceres

## 📋 Tabla de Rutas Estándar

### **1. Módulo Principal (Home/Dashboard)**
| Ruta | Método | Acción | Vista | Descripción |
|------|--------|--------|-------|-------------|
| `/` | GET | `default` | `dashboard.jsp` | Página principal del sistema |
| `/home` | GET | `default` | `dashboard.jsp` | Dashboard con resumen general |

### **2. Módulo Miembros (`/miembros`)**
| Ruta | Método | Acción | Vista | Descripción |
|------|--------|--------|-------|-------------|
| `/miembros` | GET | `list` (default) | `miembros/index.jsp` | Lista todos los miembros |
| `/miembros?action=new` | GET | `new` | `miembros/form.jsp` | Formulario nuevo miembro |
| `/miembros?action=edit&id={id}` | GET | `edit` | `miembros/form.jsp` | Formulario editar miembro |
| `/miembros?action=delete&id={id}` | GET | `delete` | - | Eliminar miembro (redirect) |
| `/miembros?action=tasks&id={id}` | GET | `tasks` | `miembros/tasks.jsp` | Tareas del miembro |
| `/miembros` | POST | `insert` | - | Crear miembro (redirect) |
| `/miembros` | POST | `update` | - | Actualizar miembro (redirect) |

### **3. Módulo Quehaceres (`/quehaceres`)**
| Ruta | Método | Acción | Vista | Descripción |
|------|--------|--------|-------|-------------|
| `/quehaceres` | GET | `list` (default) | `quehaceres/index.jsp` | Lista todas las tareas |
| `/quehaceres?action=new` | GET | `new` | `quehaceres/form.jsp` | Formulario nueva tarea |
| `/quehaceres?action=edit&id={id}` | GET | `edit` | `quehaceres/form.jsp` | Formulario editar tarea |
| `/quehaceres?action=pending` | GET | `pending` | `quehaceres/pending.jsp` | Tareas pendientes |
| `/quehaceres?action=complete` | GET | `complete` | `quehaceres/complete.jsp` | Formulario completar tarea |
| `/quehaceres?action=delete&id={id}` | GET | `delete` | - | Eliminar tarea (redirect) |
| `/quehaceres?action=listGestion` | GET | `listGestion` | `quehaceres/index.jsp` | Vista de gestión |
| `/quehaceres` | POST | `insert` | - | Crear tarea (redirect) |
| `/quehaceres` | POST | `markComplete` | - | Marcar como completada (redirect) |
| `/quehaceres` | POST | `update` | - | Actualizar tarea (redirect) |

### **4. Módulo Incentivos (`/incentivos`)**
| Ruta | Método | Acción | Vista | Descripción |
|------|--------|--------|-------|-------------|
| `/incentivos` | GET | `list` (default) | `incentivos/index.jsp` | Lista todos los incentivos |
| `/incentivos?action=new` | GET | `new` | `incentivos/form.jsp` | Formulario nuevo incentivo |
| `/incentivos?action=edit&id={id}` | GET | `edit` | `incentivos/form.jsp` | Formulario editar incentivo |
| `/incentivos?action=history` | GET | `history` | `incentivos/history.jsp` | Historial de incentivos |
| `/incentivos?action=statistics` | GET | `statistics` | `incentivos/statistics.jsp` | Estadísticas de incentivos |
| `/incentivos?action=byMiembro&miembroId={id}` | GET | `byMiembro` | `incentivos/byMiembro.jsp` | Incentivos por miembro |
| `/incentivos?action=toggle&id={id}` | GET | `toggle` | - | Activar/desactivar incentivo (redirect) |
| `/incentivos` | POST | `create` | - | Crear incentivo (redirect) |
| `/incentivos` | POST | `update` | - | Actualizar incentivo (redirect) |

---

## 🎯 Patrón de Convenciones

### **URL Structure**
```
/{modulo}?action={accion}&{parametros}
```

### **Acciones Estándar por Módulo**
| Acción | GET | POST | Descripción |
|--------|-----|------|-------------|
| `list` | ✅ | ❌ | Listar elementos (acción por defecto) |
| `new` | ✅ | ❌ | Mostrar formulario nuevo |
| `edit` | ✅ | ❌ | Mostrar formulario editar |
| `delete` | ✅ | ❌ | Eliminar elemento (redirect) |
| `insert/create` | ❌ | ✅ | Crear nuevo elemento |
| `update` | ❌ | ✅ | Actualizar elemento existente |

### **Acciones Específicas por Módulo**
- **Miembros**: `tasks` (ver tareas del miembro)
- **Quehaceres**: `pending`, `complete`, `markComplete`, `listGestion`
- **Incentivos**: `history`, `statistics`, `byMiembro`, `toggle`

---

## 📂 Estructura de Vistas (JSP)

### **Fragmentos Comunes (`/common/`)**
```
webapp/
├── common/
│   ├── layout-head.jsp      # <head> y CSS
│   ├── header.jsp           # Header y navegación
│   ├── messages.jsp         # Mensajes de éxito/error
│   ├── footer.jsp           # Footer
│   └── layout-foot.jsp      # Scripts y </body>
```

### **Estructura por Módulo**
```
webapp/
├── dashboard.jsp            # Página principal
├── miembros/
│   ├── index.jsp           # Lista de miembros
│   ├── form.jsp            # Crear/editar miembro
│   └── tasks.jsp           # Tareas del miembro
├── quehaceres/
│   ├── index.jsp           # Lista de tareas
│   ├── form.jsp            # Crear/editar tarea
│   ├── pending.jsp         # Tareas pendientes
│   └── complete.jsp        # Completar tarea
└── incentivos/
    ├── index.jsp           # Lista de incentivos
    ├── form.jsp            # Crear/editar incentivo
    ├── history.jsp         # Historial
    ├── statistics.jsp      # Estadísticas
    └── byMiembro.jsp       # Incentivos por miembro
```

---

## 🔄 Flujo de Navegación

### **Navegación Principal**
```
Dashboard (/) 
├── Miembros (/miembros)
│   ├── Nuevo (?action=new)
│   ├── Editar (?action=edit&id=X)
│   └── Tareas (?action=tasks&id=X)
├── Quehaceres (/quehaceres)
│   ├── Nuevo (?action=new)
│   ├── Pendientes (?action=pending)
│   └── Completar (?action=complete)
└── Incentivos (/incentivos)
    ├── Nuevo (?action=new)
    ├── Historial (?action=history)
    └── Estadísticas (?action=statistics)
```

### **Breadcrumbs Estándar**
```
Dashboard > {Módulo} > {Acción}
```

---

## ⚙️ Configuración Servlet

### **Mapeo de URLs (web.xml)**
```xml
<!-- HomeServlet -->
<servlet-mapping>
    <servlet-name>HomeServlet</servlet-name>
    <url-pattern></url-pattern>
    <url-pattern>/home</url-pattern>
</servlet-mapping>

<!-- MiembroServlet -->
<servlet-mapping>
    <servlet-name>MiembroServlet</servlet-name>
    <url-pattern>/miembros</url-pattern>
</servlet-mapping>

<!-- QuehacerServlet -->
<servlet-mapping>
    <servlet-name>QuehacerServlet</servlet-name>
    <url-pattern>/quehaceres</url-pattern>
</servlet-mapping>

<!-- IncentivoServlet -->
<servlet-mapping>
    <servlet-name>IncentivoServlet</servlet-name>
    <url-pattern>/incentivos</url-pattern>
</servlet-mapping>
```

---

## 🛠️ Implementación en Servlets

### **Patrón de doGet() Estándar**
```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    String action = request.getParameter("action");
    if (action == null) {
        action = "list"; // Acción por defecto
    }

    switch (action) {
        case "new":
            showNewForm(request, response);
            break;
        case "edit":
            showEditForm(request, response);
            break;
        case "delete":
            deleteElement(request, response);
            break;
        // ... acciones específicas del módulo
        default:
            listElements(request, response);
            break;
    }
}
```

### **Patrón de doPost() Estándar**
```java
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    String action = request.getParameter("action");
    
    switch (action) {
        case "insert":
        case "create":
            createElement(request, response);
            break;
        case "update":
            updateElement(request, response);
            break;
        // ... acciones específicas del módulo
        default:
            response.sendRedirect(request.getContextPath() + "/modulo");
            break;
    }
}
```

---

## 📋 Checklist de Implementación

### **✅ Completadas**
- [x] Estructura común JSP (`/common/`)
- [x] Dashboard unificado (`dashboard.jsp`)
- [x] Módulo miembros (rutas estándar)
- [x] Módulo quehaceres (rutas estándar)
- [x] Módulo incentivos (rutas básicas)

### **🔄 En Progreso**
- [ ] Completar todas las acciones de incentivos
- [ ] Validar consistencia de parámetros
- [ ] Documentar códigos de respuesta

### **⏳ Pendientes**
- [ ] Implementar manejo de errores centralizado
- [ ] Agregar validación de permisos
- [ ] Implementar paginación estándar
- [ ] Crear interceptor de logging

---

## 🎨 Convenciones de Nombres

### **Parámetros URL**
- `action`: Acción a realizar
- `id`: ID del elemento principal
- `miembroId`: ID específico de miembro
- `quehacerId`: ID específico de tarea

### **Atributos Request**
- `lista{Modulo}`: Lista principal del módulo
- `{elemento}`: Elemento individual
- `successMessage`: Mensaje de éxito
- `errorMessage`: Mensaje de error

### **Métodos Servlet**
- `list{Modulo}()`: Listar elementos
- `showNewForm()`: Mostrar formulario nuevo
- `showEditForm()`: Mostrar formulario editar
- `insert{Modulo}()`: Crear elemento
- `update{Modulo}()`: Actualizar elemento
- `delete{Modulo}()`: Eliminar elemento

---

## 📊 Sistema Observer

### **Notificaciones Automáticas**
Todas las acciones que modifican datos activan automáticamente:
- ✅ Notificación a observadores registrados
- ✅ Actualización de estadísticas
- ✅ Registro en historial
- ✅ Sincronización entre módulos

### **Puntos de Integración**
- **Crear miembro** → Suscripción automática a observadores
- **Crear tarea** → Notificación de nueva asignación
- **Completar tarea** → Cálculo automático de incentivos
- **Modificar incentivo** → Actualización de disponibilidad

---

*Documento generado el ${new Date().toLocaleDateString()} como parte del proyecto de refactorización MVC.*