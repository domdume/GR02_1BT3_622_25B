<%--
    Lista de Miembros - Solo mostrar datos del modelo
    Responsabilidad: Mostrar información proporcionada por MiembroServlet
    Sin lógica de negocio - Solo vista pura MVC
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="Listado de Logros" scope="request" />
<c:set var="bodyClass" value="achievements-page" scope="request" />
<c:set var="pageTitle" value="Gestión de Miembros" scope="request" />
<c:set var="bodyClass" value="members-page" scope="request" />


<jsp:include page="../common/layout-head.jsp" />
<jsp:include page="../common/header.jsp" />

<main class="main-content">
    <div class="container">
        <div class="page-header">
            <h2>Gestión de Miembros del Hogar</h2>
            <div class="header-actions">
                <c:if test="${sessionScope.viewRole == 'JEFE'}">
                    <a href="${pageContext.request.contextPath}/miembros?action=new" class="btn btn-primary">
                        👥 Nuevo Miembro
                    </a>
                </c:if>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">
                    🏠 Dashboard
                </a>
            </div>
        </div>

        <jsp:include page="../common/messages.jsp" />

        <!-- Estadísticas de miembros -->
        <section class="stats-section">
            <div class="stats-grid">
                <div class="stat-card">
                    <span class="stat-value">${totalMiembros}</span>
                    <span class="stat-label">Total Miembros</span>
                </div>
                <div class="stat-card">
                    <span class="stat-value">${jefeCount}</span>
                    <span class="stat-label">Jefes del Hogar</span>
                </div>
                <div class="stat-card">
                    <span class="stat-value">${totalPuntos}</span>
                    <span class="stat-label">Puntos Totales</span>
                </div>
                <div class="stat-card">
                    <span class="stat-value">${totalTareas}</span>
                    <span class="stat-label">Tareas Asignadas</span>
                </div>
            </div>
        </section>

        <!-- Lista de miembros -->
        <section class="members-list">
            <h3>Lista de Miembros</h3>

            <c:choose>
                <c:when test="${not empty listaMiembros}">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                            <tr>
                                <th>Miembro</th>
                                <th>Edad</th>
                                <th>Rol</th>
                                <th>Puntos</th>
                                <th>Emblema</th>
                                <th>Tareas</th>
                                <th>Estado Observer</th>
                                <th>Acciones</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="miembro" items="${listaMiembros}">
                                <tr class="member-row">
                                    <td class="member-info">
                                        <div class="member-avatar">
                                            <c:choose>
                                                <c:when test="${miembro.getClass().simpleName == 'JefeDelHogar'}">
                                                    👑
                                                </c:when>
                                                <c:otherwise>
                                                    👤
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="member-details">
                                            <strong><c:out value="${miembro.nombre}" /></strong>
                                            <small>ID: ${miembro.id}</small>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="age-badge">${miembro.edad} años</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${miembro.getClass().simpleName == 'JefeDelHogar'}">
                                                <span class="role-badge jefe">👑 Jefe del Hogar</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="role-badge miembro">👤 Miembro Regular</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                            <span class="points-badge points-${miembro.puntos >= 100 ? 'high' : miembro.puntos >= 50 ? 'medium' : 'low'}">
                                                🏆 ${miembro.puntos} pts
                                            </span>
                                    </td>

                                        <%-- INICIO DEL BLOQUE CORREGIDO PARA EMBLEMAS/LOGROS --%>
                                    <td>
                                        <c:if test="${not empty miembro.logros}">
                                            <div class="achievements-container" style="display: flex; flex-direction: column; gap: 4px;">
                                                    <%-- Iteramos sobre los logros para mostrarlos todos --%>
                                                <c:forEach var="logro" items="${miembro.logros}">

                                                    <%-- ACCESO CORREGIDO: Usamos las propiedades directas del objeto 'logro'.
                                                         Asumimos que tipoLogro es un ENUM y comparamos con .name() --%>
                                                    <c:set var="tipo" value="${logro.tipoLogro.name()}" />
                                                    <c:set var="nombre" value="${logro.nombre}" />

                                                    <%-- Mostramos un badge diferente según el tipo --%>
                                                    <c:choose>
                                                        <c:when test="${tipo == 'MEDALLA'}">
                                                            <span class="badge achievement medalla">🏅 ${nombre}</span>
                                                        </c:when>
                                                        <c:when test="${tipo == 'LOGRO_RACHA'}">
                                                            <span class="badge achievement racha">🔥 ${nombre}</span>
                                                        </c:when>
                                                        <c:when test="${tipo == 'EMBLEMA'}">
                                                            <span class="badge achievement emblema">🎖️ ${nombre}</span>
                                                        </c:when>
                                                        <%-- Fallback por si hay un tipo desconocido --%>
                                                        <c:otherwise>
                                                            <span class="badge achievement">⭐ ${nombre}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                            </div>
                                        </c:if>

                                            <%-- Si el miembro no tiene ningún logro, mostramos el guion --%>
                                        <c:if test="${empty miembro.logros}">
                                            —
                                        </c:if>
                                    </td>
                                        <%-- FIN DEL BLOQUE CORREGIDO --%>
                                    <td>
                                        <div class="tasks-summary">
                                            <span class="task-count">${fn:length(miembro.quehaceres)} tareas</span>
                                            <c:if test="${fn:length(miembro.quehaceres) > 0}">
                                                <small class="task-details">
                                                    <c:set var="completadas" value="0" />
                                                    <c:forEach var="quehacer" items="${miembro.quehaceres}">
                                                        <c:if test="${quehacer.completado}">
                                                            <c:set var="completadas" value="${completadas + 1}" />
                                                        </c:if>
                                                    </c:forEach>
                                                        ${completadas} completadas
                                                </small>
                                            </c:if>
                                        </div>
                                    </td>
                                    <td>
                                            <span class="observer-status active">
                                                🔔 Suscrito
                                            </span>
                                        <small class="observer-details">
                                            Recibe notificaciones automáticas
                                        </small>
                                    </td>
                                    <td class="actions">
                                        <div class="action-buttons">
                                            <a href="${pageContext.request.contextPath}/quehaceres?action=pending&miembroId=${miembro.id}"
                                               class="btn btn-sm btn-outline"
                                               title="Ver tareas pendientes">
                                                📋 Tareas
                                            </a>
                                            <c:if test="${sessionScope.viewRole == 'JEFE'}">
                                                <a href="${pageContext.request.contextPath}/miembros?action=toggleFreeze&id=${miembro.id}"
                                                   class="btn btn-sm btn-outline" title="Proteger racha">
                                                    <c:choose>
                                                        <c:when test="${miembro.rachaCongelada}">❄️ Descongelar</c:when>
                                                        <c:otherwise>❄️ Proteger Racha</c:otherwise>
                                                    </c:choose>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/miembros?action=delete&id=${miembro.id}"
                                                   class="btn btn-sm btn-outline" style="color:#fca5a5; border-color: rgba(239,68,68,0.35);"
                                                   onclick="return confirm('¿Eliminar al miembro ${miembro.nombre}? Esta acción es irreversible.');"
                                                   title="Eliminar miembro">🗑️ Eliminar</a>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <div class="empty-icon">👥</div>
                        <h3>No hay miembros registrados</h3>
                        <p>Comience registrando el primer miembro del hogar.</p>
                        <a href="${pageContext.request.contextPath}/miembros?action=new" class="btn btn-primary">
                            Registrar Primer Miembro
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</main>

<jsp:include page="../common/footer.jsp" />
<jsp:include page="../common/layout-foot.jsp" />
