<%-- 
    Formulario de Quehaceres - Solo formulario de entrada
    Responsabilidad: Capturar datos del usuario y enviar al servlet
    Sin lógica de negocio - Solo vista pura MVC
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="Crear Quehacer" scope="request" />
<c:set var="bodyClass" value="form-page" scope="request" />

<jsp:include page="../common/layout-head.jsp" />
<jsp:include page="../common/header.jsp" />

<main class="main-content">
    <div class="container">
        <div class="page-header">
            <h2>Crear Nuevo Quehacer</h2>
            <nav class="breadcrumb">
                <a href="${pageContext.request.contextPath}/home">Dashboard</a> >
                <a href="${pageContext.request.contextPath}/quehaceres">Quehaceres</a> >
                <span>Nuevo</span>
            </nav>
        </div>

        <jsp:include page="../common/messages.jsp" />

        <section class="form-section">
            <form action="${pageContext.request.contextPath}/quehaceres" method="post" class="quehacer-form">
                <input type="hidden" name="action" value="insert" />
                
                <div class="form-group">
                    <label for="nombre" class="form-label">Nombre del quehacer:</label>
                    <input type="text" 
                           id="nombre" 
                           name="nombre" 
                           class="form-input" 
                           placeholder="Ej: Limpiar la cocina, Lavar los platos..."
                           value="${param.nombre}"
                           required 
                           maxlength="200" />
                    <small class="form-help">Descripción clara de la tarea a realizar</small>
                </div>

                <div class="form-group">
                    <label for="tiempoLimite" class="form-label">Fecha y hora límite:</label>
                    <input type="datetime-local" 
                           id="tiempoLimite" 
                           name="tiempoLimite" 
                           class="form-input" 
                           value="${param.tiempoLimite}"
                           required />
                    <small class="form-help">Fecha límite para completar la tarea</small>
                </div>

                <div class="form-group">
                    <label for="dificultad" class="form-label">Nivel de dificultad:</label>
                    <select id="dificultad" name="dificultad" class="form-select" required>
                        <option value="FACIL" ${param.dificultad == 'FACIL' ? 'selected' : ''}>
                            🟢 Fácil - Tareas básicas del hogar
                        </option>
                        <option value="MEDIO" ${param.dificultad == 'MEDIO' || empty param.dificultad ? 'selected' : ''}>
                            🟡 Medio - Tareas que requieren esfuerzo moderado
                        </option>
                        <option value="DIFICIL" ${param.dificultad == 'DIFICIL' ? 'selected' : ''}>
                            🔴 Difícil - Tareas complejas o que requieren tiempo
                        </option>
                    </select>
                    <small class="form-help">La dificultad afecta los puntos de recompensa</small>
                </div>

                <div class="form-group">
                    <label for="miembroId" class="form-label">Asignar a miembro:</label>
                    <select id="miembroId" name="miembroId" class="form-select" required>
                        <option value="">-- Seleccione un miembro --</option>
                        <c:forEach var="miembro" items="${listaMiembros}">
                            <option value="${miembro.id}" 
                                    ${param.miembroId == miembro.id ? 'selected' : ''}>
                                <c:choose>
                                    <c:when test="${miembro.getClass().simpleName == 'JefeDelHogar'}">
                                        👑 ${miembro.nombre} (Jefe) - ${miembro.puntos} pts
                                    </c:when>
                                    <c:otherwise>
                                        👤 ${miembro.nombre} - ${miembro.puntos} pts
                                    </c:otherwise>
                                </c:choose>
                            </option>
                        </c:forEach>
                    </select>
                    <c:choose>
                        <c:when test="${empty listaMiembros}">
                            <small class="form-help error">
                                ⚠️ No hay miembros registrados. 
                                <a href="${pageContext.request.contextPath}/miembros?action=new">Registre el primer miembro</a>
                            </small>
                        </c:when>
                        <c:otherwise>
                            <small class="form-help">El miembro recibirá una notificación automática</small>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary" ${empty listaMiembros ? 'disabled' : ''}>
                        📋 Crear Quehacer
                    </button>
                    <a href="${pageContext.request.contextPath}/quehaceres" class="btn btn-secondary">
                        ↩️ Cancelar
                    </a>
                </div>
            </form>
        </section>

        <!-- Información del sistema Observer -->
        <aside class="info-panel">
            <h3>🔔 Sistema de Notificaciones</h3>
            <p>Cuando cree el quehacer:</p>
            <ul>
                <li>✅ El miembro asignado recibirá una notificación automática</li>
                <li>✅ Se activará el patrón Observer para avisar a todos los suscritos</li>
                <li>✅ La tarea aparecerá en la lista de pendientes del miembro</li>
                <li>✅ Se calculará automáticamente la recompensa según dificultad</li>
            </ul>
            
            <c:if test="${not empty listaMiembros}">
                <div class="members-preview">
                    <h4>👥 Miembros disponibles (${listaMiembros.size()}):</h4>
                    <c:forEach var="miembro" items="${listaMiembros}" varStatus="status">
                        <span class="member-tag ${miembro.getClass().simpleName == 'JefeDelHogar' ? 'jefe' : 'regular'}">
                            ${miembro.nombre}
                            <c:if test="${status.count < listaMiembros.size()}">, </c:if>
                        </span>
                    </c:forEach>
                </div>
            </c:if>
        </aside>
    </div>
</main>

<jsp:include page="../common/footer.jsp" />
<jsp:include page="../common/layout-foot.jsp" />

    <!-- Tabla de Quehaceres Existentes -->
    <h2>Quehaceres Registrados</h2>
    <c:choose>
        <c:when test="${not empty listaQuehaceres}">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Dificultad</th>
                        <th>Asignado a</th>
                        <th>Puntos</th>
                        <th>Fecha Límite</th>
                        <th>Estado</th>
                        <th>Recompensa / Penalización</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="quehacer" items="${listaQuehaceres}">
                        <tr>
                            <td>${quehacer.id}</td>
                            <td>${quehacer.nombre}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${quehacer.dificultad == 'FACIL'}">
                                        <span style="color: green; font-weight: bold;">🟢 Fácil</span>
                                    </c:when>
                                    <c:when test="${quehacer.dificultad == 'MEDIO'}">
                                        <span style="color: orange; font-weight: bold;">🟡 Medio</span>
                                    </c:when>
                                    <c:when test="${quehacer.dificultad == 'DIFICIL'}">
                                        <span style="color: red; font-weight: bold;">🔴 Difícil</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: gray;">❔ No definida</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${quehacer.miembroHogar.nombre}</td>
                            <td>
                                <div style="text-align: center;">
                                    <strong style="color: #2e7d32; font-size: 1.1em;">${quehacer.puntosEnEseMomento} pts</strong><br>
                                    <small style="color: #666;">
                                        <c:choose>
                                            <c:when test="${quehacer.estadoFinalizado}">
                                                Total después de esta tarea
                                            </c:when>
                                            <c:otherwise>
                                                Total actual
                                            </c:otherwise>
                                        </c:choose>
                                    </small>
                                </div>
                            </td>
                            <td>
                                <strong>${quehacer.tiempoLimite}</strong>
                                <c:if test="${quehacer.estadoFinalizado and quehacer.fechaFinalizacion != null}">
                                    <br><small style="color: #666;">
                                        <c:choose>
                                            <c:when test="${quehacer.estadoCompletado}">
                                                Completado: ${quehacer.fechaFinalizacion}
                                            </c:when>
                                            <c:otherwise>
                                                Expiró: ${quehacer.fechaFinalizacion}
                                            </c:otherwise>
                                        </c:choose>
                                    </small>
                                </c:if>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${quehacer.estadoFinalizado}">
                                        <c:choose>
                                            <c:when test="${quehacer.estadoCompletado}">
                                                <span style="color: #4caf50; font-weight: bold;">✅ Completado</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: #f44336; font-weight: bold;">❌ Atrasado</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: #ff9800; font-weight: bold;">⏳ Pendiente</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${quehacer.estadoFinalizado}">
                                        <c:choose>
                                            <c:when test="${quehacer.estadoCompletado}">
                                                <span style="color: #000; font-weight: normal;" class="recompensa-text" data-quehacer-id="${quehacer.id}">Cargando...</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: #000; font-weight: normal;" class="penalizacion-text" data-quehacer-id="${quehacer.id}">Cargando...</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: #666;">Pendiente de completar</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a href="quehaceres?action=delete&id=${quehacer.id}" 
                                   onclick="return confirm('¿Está seguro de eliminar este quehacer?')">Eliminar</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <p>No hay quehaceres registrados.</p>
        </c:otherwise>
    </c:choose>
</div>

<script>
// Listas de recompensas y penalizaciones divertidas
const recompensas = [
    "No debes lavar los platos durante una semana",
    "Libre de hacer tu cama por 3 días",
    "Puedes elegir la película de la noche",
    "No tienes que sacar la basura esta semana",
    "Día libre de cocinar",
    "Puedes quedarte despierto 1 hora extra",
    "Libre de aspirar por una semana",
    "Eliges el menú del domingo",
    "No haces limpieza del baño por 5 días",
    "Tienes el control remoto por un día completo"
];

const penalizaciones = [
    "Debes barrer durante una semana",
    "Lavar los platos todos los días por 3 días",
    "Sacar la basura durante una semana completa",
    "Limpiar el baño por 5 días seguidos",
    "Aspirar toda la casa durante una semana",
    "Hacer todas las camas por 3 días",
    "Limpiar las ventanas de toda la casa",
    "Ordenar tu cuarto todos los días por una semana",
    "Lavar y doblar ropa por 5 días",
    "Cocinar la cena durante una semana"
];

// Función para obtener un elemento aleatorio de una lista basado en un ID (para consistencia)
function getConsistentRandomItem(array, id) {
    // Usar el ID como semilla para obtener siempre el mismo resultado para el mismo quehacer
    const index = Math.abs(id) % array.length;
    return array[index];
}

// Asignar recompensas y penalizaciones cuando se carga la página
document.addEventListener('DOMContentLoaded', function() {
    // Asignar recompensas
    document.querySelectorAll('.recompensa-text').forEach(function(element) {
        const quehacerId = element.getAttribute('data-quehacer-id');
        const recompensa = getConsistentRandomItem(recompensas, parseInt(quehacerId));
        element.innerHTML = recompensa;
    });
    
    // Asignar penalizaciones
    document.querySelectorAll('.penalizacion-text').forEach(function(element) {
        const quehacerId = element.getAttribute('data-quehacer-id');
        const penalizacion = getConsistentRandomItem(penalizaciones, parseInt(quehacerId));
        element.innerHTML = penalizacion;
    });
});
</script>

</body>
</html>