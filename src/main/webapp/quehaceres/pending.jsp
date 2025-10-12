<%-- 
    Vista de Quehaceres Pendientes - Solo mostrar datos del modelo
    Responsabilidad: Mostrar tareas pendientes proporcionadas por QuehacerServlet
    Sin lógica de negocio - Solo vista pura MVC
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="Tareas Pendientes" scope="request" />
<c:set var="bodyClass" value="pending-tasks-page" scope="request" />

<jsp:include page="../common/layout-head.jsp" />
<jsp:include page="../common/header.jsp" />

<main class="main-content">
    <div class="container">
        <div class="page-header">
            <h2>Tareas Pendientes</h2>
            <nav class="breadcrumb">
                <a href="${pageContext.request.contextPath}/home">Dashboard</a> >
                <a href="${pageContext.request.contextPath}/quehaceres">Quehaceres</a> >
                <span>Pendientes</span>
            </nav>
        </div>

        <jsp:include page="../common/messages.jsp" />

        <!-- Filtro por miembro -->
        <section class="filter-section">
            <h3>🔍 Filtrar por miembro</h3>
            <form action="${pageContext.request.contextPath}/quehaceres" method="get" class="filter-form">
                <input type="hidden" name="action" value="pending" />
                
                <div class="form-group inline">
                    <label for="miembroId" class="form-label">Seleccionar miembro:</label>
                    <select id="miembroId" name="miembroId" class="form-select" onchange="this.form.submit()">
                        <option value="">-- Todos los miembros --</option>
                        <c:forEach var="miembro" items="${listaMiembros}">
                            <option value="${miembro.id}" 
                                    ${param.miembroId == miembro.id ? 'selected' : ''}>
                                <c:choose>
                                    <c:when test="${miembro.getClass().simpleName == 'JefeDelHogar'}">
                                        👑 ${miembro.nombre} (${fn:length(miembro.quehaceres)} tareas)
                                    </c:when>
                                    <c:otherwise>
                                        👤 ${miembro.nombre} (${fn:length(miembro.quehaceres)} tareas)
                                    </c:otherwise>
                                </c:choose>
                            </option>
                        </c:forEach>
                    </select>
                    
                    <c:if test="${empty listaMiembros}">
                        <small class="form-help error">
                            No hay miembros registrados. 
                            <a href="${pageContext.request.contextPath}/miembros?action=new">Registrar miembro</a>
                        </small>
                    </c:if>
                </div>
            </form>
        </section>

        <!-- Información del miembro seleccionado -->
        <c:if test="${not empty miembroSeleccionado}">
            <section class="member-info">
                <c:forEach var="miembro" items="${listaMiembros}">
                    <c:if test="${miembro.id == miembroSeleccionado}">
                        <div class="member-card selected">
                            <div class="member-avatar">
                                <c:choose>
                                    <c:when test="${miembro.getClass().simpleName == 'JefeDelHogar'}">👑</c:when>
                                    <c:otherwise>👤</c:otherwise>
                                </c:choose>
                            </div>
                            <div class="member-details">
                                <h3>${miembro.nombre}</h3>
                                <div class="member-stats">
                                    <span class="stat">🏆 ${miembro.puntos} puntos</span>
                                    <span class="stat">📋 ${fn:length(miembro.quehaceres)} tareas totales</span>
                                    <span class="stat">
                                        ⏳ 
                                        <c:set var="pendientesCount" value="0" />
                                        <c:forEach var="tarea" items="${miembro.quehaceres}">
                                            <c:if test="${not tarea.estadoCompletado}">
                                                <c:set var="pendientesCount" value="${pendientesCount + 1}" />
                                            </c:if>
                                        </c:forEach>
                                        ${pendientesCount} pendientes
                                    </span>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
            </section>
        </c:if>

        <!-- Lista de tareas pendientes -->
        <section class="pending-tasks">
            <c:choose>
                <c:when test="${empty miembroSeleccionado}">
                    <div class="info-message">
                        <h3>👋 Seleccione un miembro</h3>
                        <p>Utilice el filtro anterior para ver las tareas pendientes de un miembro específico.</p>
                    </div>
                </c:when>
                <c:when test="${empty tareasPendientes}">
                    <div class="success-message">
                        <h3>🎉 ¡Excelente trabajo!</h3>
                        <p>No hay tareas pendientes para este miembro. Todas las tareas están completadas.</p>
                        <a href="${pageContext.request.contextPath}/quehaceres?action=new" class="btn btn-primary">
                            Crear nueva tarea
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <h3>⏳ Tareas pendientes (${fn:length(tareasPendientes)})</h3>
                    
                    <div class="tasks-grid">
                        <c:forEach var="tarea" items="${tareasPendientes}">
                            <div class="task-card pending">
                                <div class="task-header">
                                    <h4 class="task-title">
                                        <c:out value="${tarea.nombre}" />
                                    </h4>
                                    <span class="difficulty-badge ${tarea.dificultad.toString().toLowerCase()}">
                                        <c:choose>
                                            <c:when test="${tarea.dificultad == 'FACIL'}">🟢 Fácil</c:when>
                                            <c:when test="${tarea.dificultad == 'MEDIO'}">🟡 Medio</c:when>
                                            <c:when test="${tarea.dificultad == 'DIFICIL'}">🔴 Difícil</c:when>
                                        </c:choose>
                                    </span>
                                </div>
                                
                                <div class="task-details">
                                    <c:if test="${not empty tarea.tiempoLimite}">
                                        <div class="task-deadline">
                                            📅 <strong>Fecha límite:</strong>
                                            <fmt:formatDate value="${tarea.tiempoLimite}" pattern="dd/MM/yyyy HH:mm" />
                                            
                                            <%-- Verificar si está vencida --%>
                                            <jsp:useBean id="now" class="java.util.Date" />
                                            <c:if test="${tarea.tiempoLimite < now}">
                                                <span class="overdue-badge">⚠️ Vencida</span>
                                            </c:if>
                                        </div>
                                    </c:if>
                                    
                                    <c:if test="${not empty tarea.recompensa}">
                                        <div class="task-reward">
                                            🏆 <strong>Recompensa:</strong> ${tarea.recompensa}
                                        </div>
                                    </c:if>
                                </div>
                                
                                <div class="task-actions">
                                    <a href="${pageContext.request.contextPath}/quehaceres?action=complete&quehacerId=${tarea.id}" 
                                       class="btn btn-success btn-sm">
                                        ✅ Marcar como completada
                                    </a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <!-- Estadísticas generales -->
        <c:if test="${not empty tareasPendientes}">
            <aside class="stats-panel">
                <h3>📊 Estadísticas</h3>
                <div class="stats-grid">
                    <div class="stat-item">
                        <span class="stat-value">${fn:length(tareasPendientes)}</span>
                        <span class="stat-label">Tareas pendientes</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-value">
                            <c:set var="vencidas" value="0" />
                            <jsp:useBean id="now2" class="java.util.Date" />
                            <c:forEach var="tarea" items="${tareasPendientes}">
                                <c:if test="${tarea.tiempoLimite < now2}">
                                    <c:set var="vencidas" value="${vencidas + 1}" />
                                </c:if>
                            </c:forEach>
                            ${vencidas}
                        </span>
                        <span class="stat-label">Tareas vencidas</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-value">
                            <c:set var="totalRecompensas" value="0" />
                            <c:forEach var="tarea" items="${tareasPendientes}">
                                <c:choose>
                                    <c:when test="${tarea.dificultad == 'FACIL'}">
                                        <c:set var="totalRecompensas" value="${totalRecompensas + 5}" />
                                    </c:when>
                                    <c:when test="${tarea.dificultad == 'MEDIO'}">
                                        <c:set var="totalRecompensas" value="${totalRecompensas + 10}" />
                                    </c:when>
                                    <c:when test="${tarea.dificultad == 'DIFICIL'}">
                                        <c:set var="totalRecompensas" value="${totalRecompensas + 15}" />
                                    </c:when>
                                </c:choose>
                            </c:forEach>
                            ${totalRecompensas}
                        </span>
                        <span class="stat-label">Puntos potenciales</span>
                    </div>
                </div>
            </aside>
        </c:if>
    </div>
</main>

<jsp:include page="../common/footer.jsp" />
<jsp:include page="../common/layout-foot.jsp" />