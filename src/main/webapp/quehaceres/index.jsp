<%-- 
    Vista Principal de Quehaceres - Solo visualización de datos
    Responsabilidad: Mostrar lista de tareas, navegación y estadísticas
    Sin lógica de negocio - Solo vista pura MVC
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="Gestión de Quehaceres" scope="request" />
<c:set var="bodyClass" value="quehaceres-index-page" scope="request" />

<jsp:include page="../common/layout-head.jsp" />
<jsp:include page="../common/header.jsp" />

<main class="main-content">
    <div class="container">
        <div class="page-header">
            <h2>📋 Gestión de Quehaceres</h2>
            <nav class="breadcrumb">
                <a href="${pageContext.request.contextPath}/home">Dashboard</a> >
                <span>Quehaceres</span>
            </nav>
        </div>

        <jsp:include page="../common/messages.jsp" />

        <!-- Panel de acciones principales -->
        <c:if test="${sessionScope.viewRole == 'JEFE'}">
            <section class="actions-panel">
                <div class="actions-grid">
                    <a href="${pageContext.request.contextPath}/quehaceres?action=new" class="action-card primary">
                        <span class="action-icon">➕</span>
                        <h3>Crear Tarea</h3>
                        <p>Asignar nueva tarea a un miembro</p>
                    </a>
                    <a href="${pageContext.request.contextPath}/quehaceres?action=pending" class="action-card pending">
                        <span class="action-icon">📋</span>
                        <h3>Tareas Pendientes</h3>
                        <p>Ver todas las tareas por completar</p>
                    </a>
                    <a href="${pageContext.request.contextPath}/quehaceres?action=complete" class="action-card success">
                        <span class="action-icon">✅</span>
                        <h3>Marcar Completada</h3>
                        <p>Registrar tarea terminada</p>
                    </a>
                </div>
            </section>
        </c:if>
        <c:if test="${sessionScope.viewRole != 'JEFE'}">
            <section class="actions-panel">
                <div class="actions-grid">
                    <a href="${pageContext.request.contextPath}/quehaceres?action=pending" class="action-card pending">
                        <span class="action-icon">📋</span>
                        <h3>Tareas Pendientes</h3>
                        <p>Ver todas las tareas por completar</p>
                    </a>
                    <a href="${pageContext.request.contextPath}/quehaceres?action=complete" class="action-card success">
                        <span class="action-icon">✅</span>
                        <h3>Marcar Completada</h3>
                        <p>Registrar tarea terminada</p>
                    </a>
                </div>
            </section>
        </c:if>

        <!-- Estadísticas generales -->
        <section class="stats-overview">
            <h3>📊 Resumen de Tareas</h3>
            <div class="stats-grid">
                <div class="stat-card">
                    <span class="stat-number">${totalTareas}</span>
                    <span class="stat-label">Total de Tareas</span>
                </div>
                
                <div class="stat-card pending">
                    <span class="stat-number">${tareasPendientes}</span>
                    <span class="stat-label">Pendientes</span>
                </div>
                
                <div class="stat-card completed">
                    <span class="stat-number">${tareasCompletadas}</span>
                    <span class="stat-label">Completadas</span>
                </div>
                
                <div class="stat-card overdue">
                    <span class="stat-number">${tareasVencidas}</span>
                    <span class="stat-label">Vencidas</span>
                </div>
            </div>
        </section>

        <!-- Lista principal de tareas -->
        <section class="tasks-list-section">
            <div class="section-header">
                <h3>📝 Todas las Tareas</h3>
                <div class="filters">
                    <button class="filter-btn active" onclick="filterTasks(event, 'all')">Todas</button>
                    <button class="filter-btn" onclick="filterTasks(event, 'pending')">Pendientes</button>
                    <button class="filter-btn" onclick="filterTasks(event, 'completed')">Completadas</button>
                    <button class="filter-btn" onclick="filterTasks(event, 'overdue')">Vencidas</button>
                </div>
            </div>

            <c:choose>
                <c:when test="${empty listaQuehaceres or fn:length(listaQuehaceres) == 0}">
                    <div class="empty-state">
                        <h4>🏠 No hay tareas registradas</h4>
                        <p>Comience creando la primera tarea del hogar</p>
                        <c:if test="${sessionScope.viewRole == 'JEFE'}">
                            <a href="${pageContext.request.contextPath}/quehaceres?action=new" class="btn btn-primary">
                                ➕ Crear Primera Tarea
                            </a>
                        </c:if>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="tasks-grid">
                        <c:forEach var="quehacer" items="${listaQuehaceres}">
                            <c:set var="isOverdue" value="${quehacer.vencido}" />

                            <div class="task-card ${quehacer.completado ? 'completed' : 'pending'} ${isOverdue ? 'overdue' : ''}"
                                 data-status="${quehacer.completado ? 'completed' : 'pending'}"
                                 data-overdue="${isOverdue}">
                                
                                <div class="task-header">
                                    <h4 class="task-title">${quehacer.nombre}</h4>
                                    <span class="task-status">
                                        <c:choose>
                                            <c:when test="${quehacer.completado}">✅</c:when>
                                            <c:when test="${isOverdue}">⏰</c:when>
                                            <c:otherwise>📋</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>

                                <div class="task-body">
                                    <div class="task-meta">
                                        <span class="task-assignee">
                                            👤 ${quehacer.miembroHogar.nombre}
                                        </span>
                                        <span class="task-difficulty ${quehacer.dificultad.toString().toLowerCase()}">
                                            <c:choose>
                                                <c:when test="${quehacer.dificultad == 'FACIL'}">🟢 Fácil</c:when>
                                                <c:when test="${quehacer.dificultad == 'MEDIO'}">🟡 Medio</c:when>
                                                <c:when test="${quehacer.dificultad == 'DIFICIL'}">🔴 Difícil</c:when>
                                            </c:choose>
                                        </span>
                                    </div>


                                    <div class="task-timing">
                                        <div class="deadline">
                                            📅 <strong>Límite:</strong> 
                                            <c:out value="${empty quehacer.tiempoLimiteFmt ? quehacer.tiempoLimite : quehacer.tiempoLimiteFmt}" />
                                        </div>
                                        
                                        <c:if test="${quehacer.completado and not empty quehacer.fechaFinalizacion}">
                                            <div class="completion-time">
                                                ✅ <strong>Completada:</strong> 
                                                <c:out value="${empty quehacer.fechaFinalizacionFmt ? quehacer.fechaFinalizacion : quehacer.fechaFinalizacionFmt}" />
                                            </div>
                                        </c:if>
                                    </div>

                                    <c:if test="${not empty quehacer.recompensa}">
                                        <div class="task-reward">
                                            🏆 <strong>Recompensa:</strong> ${quehacer.recompensa}
                                        </div>
                                    </c:if>
                                </div>

                                <div class="task-actions">
                                    <c:choose>
                                        <c:when test="${quehacer.completado}">
                                            <span class="status-badge completed">✅ Completada</span>
                                        </c:when>
                                        <c:otherwise>
                                            <c:if test="${sessionScope.viewRole == 'JEFE'}">
                                            <a href="${pageContext.request.contextPath}/quehaceres?action=complete&quehacerId=${quehacer.id}" 
                                               class="btn btn-success btn-sm">
                                                ✅ Completar
                                            </a>
                                            </c:if>
                                            <c:if test="${sessionScope.viewRole == 'JEFE'}">
                                            <a href="${pageContext.request.contextPath}/quehaceres?action=edit&id=${quehacer.id}" 
                                               class="btn btn-outline btn-sm">
                                                ✏️ Editar
                                            </a>
                                            </c:if>
                                            <c:if test="${sessionScope.viewRole == 'JEFE'}">
                                                <a href="${pageContext.request.contextPath}/quehaceres?action=delete&id=${quehacer.id}"
                                                   class="btn btn-outline btn-sm" style="color:#fca5a5; border-color: rgba(239,68,68,0.35);"
                                                   onclick="return confirm('¿Eliminar la tarea ${quehacer.nombre}? Esta acción es irreversible.');">🗑️ Eliminar</a>
                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <!-- Resumen por miembro -->
        <c:if test="${not empty listaQuehaceres}">
            <section class="member-summary">
                <h3>👥 Resumen por Miembro</h3>
                <div class="member-stats-grid">
                    <!-- Calcular estadísticas por miembro usando JSTL -->
                    <c:set var="membersMap" value="${java.util.HashMap()}" />
                    <c:forEach var="quehacer" items="${listaQuehaceres}">
                        <c:set var="memberName" value="${quehacer.miembroHogar.nombre}" />
                        <!-- Lógica simplificada para vista - stats completas vienen del servlet -->
                    </c:forEach>
                    
                    <!-- Por ahora mostramos un placeholder - las estadísticas detalladas vendrán del servlet -->
                    <div class="placeholder-stats">
                        <p>📊 Las estadísticas detalladas por miembro se cargarán desde el servlet...</p>
                        <small>Esta vista solo muestra datos, el cálculo se hace en la lógica de negocio</small>
                    </div>
                </div>
            </section>
        </c:if>
    </div>
</main>

<!-- JavaScript para filtros dinámicos -->
<script>
function filterTasks(event, status) {
    const cards = document.querySelectorAll('.task-card');
    const buttons = document.querySelectorAll('.filter-btn');
    
    // Actualizar botones activos
    buttons.forEach(btn => btn.classList.remove('active'));
    if (event && event.target) event.target.classList.add('active');

    // Filtrar tarjetas
    cards.forEach(card => {
        const cardStatus = card.dataset.status;
        const isOverdue = card.dataset.overdue === 'true';
        
        let shouldShow = false;
        
        switch(status) {
            case 'all':
                shouldShow = true;
                break;
            case 'pending':
                shouldShow = cardStatus === 'pending' && !isOverdue;
                break;
            case 'completed':
                shouldShow = cardStatus === 'completed';
                break;
            case 'overdue':
                shouldShow = isOverdue;
                break;
        }
        
        card.style.display = shouldShow ? 'block' : 'none';
    });
}

// Inicializar componentes al cargar
document.addEventListener('DOMContentLoaded', function() {
    console.log('Vista de Quehaceres cargada - Solo visualización de datos');
});
</script>

<jsp:include page="../common/footer.jsp" />
<jsp:include page="../common/layout-foot.jsp" />
