<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%--
    Dashboard Principal - Vista unificada que reemplaza tablero.jsp
    Responsabilidad: Solo mostrar datos proporcionados por HomeServlet
    Sin lógica de negocio - Solo vista pura MVC
--%>
<c:set var="pageTitle" value="Dashboard del Hogar" scope="request" />
<c:set var="bodyClass" value="dashboard-page" scope="request" />

<jsp:include page="common/layout-head.jsp" />
<jsp:include page="common/header.jsp" />

<main class="main-content">
    <div class="container">
        <jsp:include page="common/messages.jsp" />

        <!-- Estadísticas rápidas -->
        <section class="section-card">
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-value">${tareasCompletadas}</div>
                    <div class="stat-label">✅ Completadas</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${tareasPendientes}</div>
                    <div class="stat-label">⏳ Pendientes</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${tareasVencidas}</div>
                    <div class="stat-label">❌ Vencidas</div>
                </div>
                <c:if test="${not empty mvpMiembro}">
                    <div class="stat-card">
                        <div class="stat-value">${mvpMiembro.puntos} pts</div>
                        <div class="stat-label">👑 MVP: ${mvpMiembro.nombre}</div>
                    </div>
                </c:if>
            </div>
        </section>

        <!-- Panel de acciones rápidas del Jefe del Hogar -->
        <c:if test="${sessionScope.viewRole == 'JEFE'}">
            <section class="section-card quick-actions">
                <h2>Acciones del Jefe del Hogar</h2>
                <div class="action-buttons">
                    <a href="${pageContext.request.contextPath}/miembros?action=new" class="btn btn-primary">👥 Registrar Miembro</a>
                    <a href="${pageContext.request.contextPath}/quehaceres?action=new" class="btn btn-primary">📋 Crear Quehacer</a>
                </div>
            </section>
        </c:if>

        <!-- Panel de acciones para miembros -->
        <section class="section-card member-actions">
            <h2>Acciones de Miembros</h2>
            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/quehaceres?action=pending" class="btn btn-secondary">⏳ Ver Pendientes</a>
                <a href="${pageContext.request.contextPath}/quehaceres?action=complete" class="btn btn-secondary">✅ Completar Tarea</a>
            </div>
        </section>

        <!-- Resumen de quehaceres del hogar -->
        <section class="section-card quehaceres-summary">
            <h2>📋 Quehaceres del Hogar</h2>

            <c:choose>
                <c:when test="${not empty listaQuehaceres}">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Quehacer</th>
                                    <th>Asignado a</th>
                                    <th>Dificultad</th>
                                    <th>Estado</th>
                                    <th>Fecha Límite</th>
                                    <th>Puntos</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="quehacer" items="${listaQuehaceres}">
                                    <c:set var="isOverdue" value="${quehacer.vencido}" />
                                    <tr class="${quehacer.completado ? 'completed' : (isOverdue ? 'overdue' : 'pending')}">
                                        <td>
                                            <span style="display:flex; align-items:center; gap:8px; font-weight:700;">
                                                ${quehacer.completado ? '✅' : (isOverdue ? '❌' : '🧹')}
                                                <c:out value="${quehacer.nombre}" />
                                            </span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty quehacer.miembroHogar}">
                                                    <c:out value="${quehacer.miembroHogar.nombre}" />
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-badge overdue">Sin asignar</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <span class="difficulty-badge ${quehacer.dificultad.toString().toLowerCase()}">
                                                <c:out value="${quehacer.dificultad}" />
                                            </span>
                                        </td>
                                        <td>
                                            <span class="status-badge ${quehacer.completado ? 'completed' : (isOverdue ? 'overdue' : 'pending')}">
                                                ${quehacer.completado ? '✅ Completado' : (isOverdue ? '❌ Vencido' : '⏳ Pendiente')}
                                            </span>
                                        </td>
                                        <td>
                                            <c:if test="${not empty quehacer.tiempoLimite}">
                                                <c:out value="${empty quehacer.tiempoLimiteFmt ? quehacer.tiempoLimite : quehacer.tiempoLimiteFmt}" />
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${not empty quehacer.miembroHogar}">
                                                <span class="points-badge">
                                                    ${quehacer.miembroHogar.puntos} pts
                                                </span>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <p>No hay quehaceres registrados en el hogar.</p>
                        <c:if test="${tieneJefe}">
                            <a href="${pageContext.request.contextPath}/quehaceres?action=new" class="btn btn-primary">Crear primer quehacer</a>
                        </c:if>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <!-- Resumen de miembros del hogar -->
        <section class="section-card members-summary">
            <h2>👥 Miembros del Hogar</h2>

            <c:choose>
                <c:when test="${not empty listaMiembros}">
                    <div class="members-grid">
                        <c:forEach var="miembro" items="${listaMiembros}">
                            <div class="member-card">
                                <div class="member-info">
                                    <h3><c:out value="${miembro.nombre}" /></h3>
                                    <p class="member-details">${miembro.edad} años | ${miembro.puntos} puntos</p>
                                    <c:if test="${miembro.getClass().simpleName == 'JefeDelHogar'}">
                                        <span class="role-badge jefe">👑 Jefe del Hogar</span>
                                    </c:if>
                                </div>
                                <div class="member-stats">
                                    <span class="stat">📋 ${fn:length(miembro.quehaceres)} tareas</span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <p>No hay miembros registrados en el hogar.</p>
                        <a href="${pageContext.request.contextPath}/miembros?action=new" class="btn btn-primary">Registrar primer miembro</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <!-- Estadísticas del sistema Observer -->
        <c:if test="${not empty observerStats}">
            <section class="section-card observer-stats">
                <h2>Estado del Sistema Observer</h2>
                <div class="stats-grid">
                    <div class="stat-card">
                        <span class="stat-value">${observerStats.observadoresSuscritos}</span>
                        <span class="stat-label">Observadores Suscritos</span>
                    </div>
                    <div class="stat-card">
                        <span class="stat-value">${observerStats.notificacionesEnviadas}</span>
                        <span class="stat-label">Notificaciones Enviadas</span>
                    </div>
                    <div class="stat-card">
                        <span class="stat-value">${observerStats.quehaceresEnCola}</span>
                        <span class="stat-label">Tareas en Cola</span>
                    </div>
                </div>
            </section>
        </c:if>

        <!-- Top por liga -->
        <section class="section-card league-tops">
            <h2>🏆 Líderes por Liga</h2>
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-value"><c:out value="${empty topBronce ? '-' : topBronce.nombre}" /></div>
                    <div class="stat-label">🥉 Bronce <c:if test="${not empty topBronce}">— ${topBronce.puntos} pts</c:if></div>
                </div>
                <div class="stat-card">
                    <div class="stat-value"><c:out value="${empty topPlata ? '-' : topPlata.nombre}" /></div>
                    <div class="stat-label">🥈 Plata <c:if test="${not empty topPlata}">— ${topPlata.puntos} pts</c:if></div>
                </div>
                <div class="stat-card">
                    <div class="stat-value"><c:out value="${empty topOro ? '-' : topOro.nombre}" /></div>
                    <div class="stat-label">🥇 Oro <c:if test="${not empty topOro}">— ${topOro.puntos} pts</c:if></div>
                </div>
            </div>
            <div style="margin-top:10px">
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/ranking">Ver ranking completo</a>
            </div>
        </section>

        <!-- NUEVO: Sección de Logros -->
        <section class="section-card" id="logros">
            <h2>🏅 Logros</h2>
            <c:choose>
                <c:when test="${not empty listaMiembros}">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Miembro</th>
                                    <th>Cantidad de Logros</th>
                                    <th>Listado</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="m" items="${listaMiembros}">
                                    <c:set var="items" value="${logrosPorMiembro[m.id]}" />
                                    <tr>
                                        <td><c:out value="${m.nombre}" /></td>
                                        <td><span class="points-badge">${empty items ? 0 : fn:length(items)}</span></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${empty items}">
                                                    <span class="status-badge pending">Sin logros aún</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <ul style="margin:0; padding-left: 18px;">
                                                        <c:forEach var="a" items="${items}">
                                                            <li>
                                                                <span class="status-badge ${a.tipoLogro == 'LOGRO_RACHA' ? 'completed' : ''}">
                                                                    <c:choose>
                                                                        <c:when test="${a.tipoLogro == 'LOGRO_RACHA'}">🔥 Racha</c:when>
                                                                        <c:otherwise>🏅 ${a.tipoLogro}</c:otherwise>
                                                                    </c:choose>
                                                                </span>
                                                                <span style="margin-left:8px; color: var(--muted);">ID: ${a.logroId}</span>
                                                            </li>
                                                        </c:forEach>
                                                    </ul>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">No hay miembros para mostrar logros.</div>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</main>

<jsp:include page="common/footer.jsp" />
<jsp:include page="common/layout-foot.jsp" />

<jsp:include page="/common/achievement-toast.jsp" />

<script>
// Si la URL trae #logros, hacemos un scroll suave al cargar
if (window.location.hash === '#logros') {
    window.addEventListener('load', function(){
        var el = document.getElementById('logros');
        if (el) el.scrollIntoView({behavior:'smooth'});
    });
}
</script>
