<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="Tablero del Hogar" scope="request" />
<c:set var="bodyClass" value="dashboard-page" scope="request" />

<jsp:include page="common/layout-head.jsp" />
<jsp:include page="common/header.jsp" />

<main class="main-content">
    <div class="container">
        <jsp:include page="common/messages.jsp" />

        <section class="section-card">
            <div style="display:flex; align-items:center; justify-content:space-between; gap:16px; flex-wrap: wrap;">
                <h2 style="margin:0; display:flex; align-items:center; gap:8px;">🏠 Tablero del Hogar</h2>
                <div class="action-buttons">
                    <c:if test="${sessionScope.viewRole == 'JEFE'}">
                        <a href="${pageContext.request.contextPath}/miembros?action=new" class="btn btn-primary">👥 Registrar Miembro</a>
                        <a href="${pageContext.request.contextPath}/quehaceres?action=new" class="btn btn-primary">📋 Crear Quehacer</a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/quehaceres?action=complete" class="btn btn-secondary">✅ Completar</a>
                    <a href="${pageContext.request.contextPath}/quehaceres?action=pending" class="btn btn-secondary">⏳ Pendientes</a>
                </div>
            </div>
        </section>

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

        <!-- Tabla de quehaceres -->
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
                                    <th>Puntos del Miembro</th>
                                    <th>Fecha Límite</th>
                                    <th>Estado</th>
                                    <th>Recompensa / Penalización</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="q" items="${listaQuehaceres}">
                                    <tr class="${q.estadoFinalizado ? (q.completado ? 'completed' : 'overdue') : ''}">
                                        <td>
                                            <span style="display:flex; align-items:center; gap:8px; font-weight:700;">
                                                ${q.completado ? '✅' : (q.estadoFinalizado ? '❌' : '🧹')}
                                                <c:out value="${q.nombre}" />
                                            </span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty q.miembroHogar}">
                                                    <c:out value="${q.miembroHogar.nombre}" />
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-badge overdue">Sin asignar</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div style="display:flex; flex-direction:column; align-items:flex-start; gap:4px;">
                                                <span class="points-badge">${q.miembroHogar.puntos} pts</span>
                                                <small style="color: var(--muted);">
                                                    <c:choose>
                                                            <c:when test="${q.estadoFinalizado}">Total después de esta tarea</c:when>
                                                            <c:otherwise>Total actual</c:otherwise>
                                                        </c:choose>
                                                </small>
                                            </div>
                                        </td>
                                        <td>
                                            <strong><c:out value="${q.tiempoLimite}" /></strong>
                                            <c:if test="${q.estadoFinalizado and q.fechaFinalizacion != null}">
                                                <br>
                                                <small style="color: var(--muted);">
                                                    <c:choose>
                                                        <c:when test="${q.completado}">Completado: ${q.fechaFinalizacion}</c:when>
                                                        <c:otherwise>Expiró: ${q.fechaFinalizacion}</c:otherwise>
                                                    </c:choose>
                                                </small>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${q.estadoFinalizado}">
                                                    <span class="status-badge ${q.completado ? 'completed' : 'overdue'}">
                                                        ${q.completado ? '✅ Completado' : '❌ Atrasado'}
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-badge pending">⏳ Pendiente</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${q.estadoFinalizado}">
                                                    <c:choose>
                                                        <c:when test="${q.completado}">
                                                            <span class="incentive-text" data-quehacer-id="${q.id}" data-type="reward">Cargando...</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="incentive-text" data-quehacer-id="${q.id}" data-type="penalty">Cargando...</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:when>
                                                <c:otherwise>
                                                    <span style="color: var(--muted);">—</span>
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
                    <div class="empty-state">
                        <h3>📋 No hay quehaceres registrados</h3>
                        <p>
                            <c:choose>
                                <c:when test="${sessionScope.viewRole == 'JEFE'}">
                                    Comienza registrando miembros de la familia y asignando quehaceres.
                                </c:when>
                                <c:otherwise>
                                    Por ahora no hay tareas publicadas. Puedes revisar pendientes o marcar completadas.
                                </c:otherwise>
                            </c:choose>
                        </p>
                        <div class="action-buttons" style="justify-content:center;">
                            <c:if test="${sessionScope.viewRole == 'JEFE'}">
                                <a href="${pageContext.request.contextPath}/miembros?action=new" class="btn btn-primary">👥 Registrar Miembro</a>
                                <a href="${pageContext.request.contextPath}/quehaceres?action=new" class="btn btn-primary">📋 Crear Quehacer</a>
                            </c:if>
                            <a href="${pageContext.request.contextPath}/quehaceres?action=complete" class="btn btn-secondary">✅ Completar</a>
                            <a href="${pageContext.request.contextPath}/quehaceres?action=pending" class="btn btn-secondary">⏳ Pendientes</a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <!-- NUEVO: Sección de Logros en Tablero -->
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

<script>
// Minimal incentives UI: show one deterministic reward/penalty text per finalised task
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

function getConsistentRandomItem(array, id) {
    const index = Math.abs(Number(id)) % array.length;
    return array[index];
}

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.incentive-text').forEach(function(el) {
        const id = el.getAttribute('data-quehacer-id');
        const type = el.getAttribute('data-type');
        if (!id || !type) return;
        if (type === 'reward') {
            el.textContent = '🏅 ' + getConsistentRandomItem(recompensas, id);
        } else {
            el.textContent = '⚠️ ' + getConsistentRandomItem(penalizaciones, id);
        }
    });

    if (window.location.hash === '#logros') {
        var el = document.getElementById('logros');
        if (el) el.scrollIntoView({behavior: 'smooth'});
    }
});
</script>

<jsp:include page="/common/achievement-toast.jsp" />
