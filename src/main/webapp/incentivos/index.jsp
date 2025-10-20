<%-- 
    Vista Principal de Incentivos - Solo visualización de datos
    Responsabilidad: Mostrar lista de incentivos, estadísticas y navegación
    Sin lógica de negocio - Solo vista pura MVC
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="Gestión de Incentivos" scope="request" />
<c:set var="bodyClass" value="incentivos-index-page" scope="request" />

<jsp:include page="../common/layout-head.jsp" />
<jsp:include page="../common/header.jsp" />

<main class="main-content">
    <div class="container">
        <div class="page-header">
            <h2>🏆 Gestión de Incentivos</h2>
            <nav class="breadcrumb">
                <a href="${pageContext.request.contextPath}/home">Dashboard</a> >
                <span>Incentivos</span>
            </nav>
        </div>

        <jsp:include page="../common/messages.jsp" />

        <!-- Panel de acciones principales -->
        <section class="actions-panel">
            <div class="actions-grid">
                <a href="${pageContext.request.contextPath}/incentivos?action=new" class="action-card primary">
                    <span class="action-icon">➕</span>
                    <h3>Crear Incentivo</h3>
                    <p>Definir nuevo incentivo o recompensa</p>
                </a>
                
                <a href="${pageContext.request.contextPath}/incentivos?action=history" class="action-card info">
                    <span class="action-icon">📚</span>
                    <h3>Historial</h3>
                    <p>Ver incentivos otorgados</p>
                </a>
                
                <a href="${pageContext.request.contextPath}/incentivos?action=statistics" class="action-card success">
                    <span class="action-icon">📊</span>
                    <h3>Estadísticas</h3>
                    <p>Análisis de incentivos</p>
                </a>
            </div>
        </section>

        <!-- Estadísticas generales -->
        <section class="stats-overview">
            <h3>📊 Resumen de Incentivos</h3>
            <div class="stats-grid">
                <c:set var="totalIncentivos" value="${fn:length(listaIncentivos)}" />
                <c:set var="incentivosPendientes" value="0" />
                <c:set var="incentivosOtorgados" value="0" />
                <c:set var="puntosAcumulados" value="0" />
                
                <c:forEach var="incentivo" items="${listaIncentivos}">
                    <c:choose>
                        <c:when test="${incentivo.tipoIncentivo == 'RECOMPENSA'}">
                            <c:set var="incentivosOtorgados" value="${incentivosOtorgados + 1}" />
                            <c:set var="puntosAcumulados" value="${puntosAcumulados + incentivo.puntos}" />
                        </c:when>
                        <c:when test="${incentivo.tipoIncentivo == 'PENALIZACION'}">
                            <c:set var="incentivosPendientes" value="${incentivosPendientes + 1}" />
                            <c:set var="puntosAcumulados" value="${puntosAcumulados + incentivo.puntos}" />
                        </c:when>
                    </c:choose>
                </c:forEach>

                <div class="stat-card">
                    <span class="stat-number">${totalIncentivos}</span>
                    <span class="stat-label">Total Incentivos</span>
                </div>
                
                <div class="stat-card success">
                    <span class="stat-number">${incentivosOtorgados}</span>
                    <span class="stat-label">Recompensas</span>
                </div>
                
                <div class="stat-card warning">
                    <span class="stat-number">${incentivosPendientes}</span>
                    <span class="stat-label">Penalizaciones</span>
                </div>
                
                <div class="stat-card info">
                    <span class="stat-number">${puntosAcumulados}</span>
                    <span class="stat-label">Balance Puntos</span>
                </div>
            </div>
        </section>

        <!-- Lista de incentivos disponibles/definidos -->
        <section class="incentivos-definitions">
            <div class="section-header">
                <h3>🎁 Incentivos Disponibles</h3>
                <div class="filters">
                    <button class="filter-btn active" onclick="filterIncentivos('all')">Todos</button>
                    <button class="filter-btn" onclick="filterIncentivos('MONETARIO')">💰 Monetarios</button>
                    <button class="filter-btn" onclick="filterIncentivos('EXPERIENCIA')">🎉 Experiencias</button>
                    <button class="filter-btn" onclick="filterIncentivos('PRIVILEGIO')">⭐ Privilegios</button>
                    <button class="filter-btn" onclick="filterIncentivos('RECONOCIMIENTO')">🏆 Reconocimientos</button>
                </div>
            </div>

            <c:choose>
                <c:when test="${empty listaIncentivosTipo or fn:length(listaIncentivosTipo) == 0}">
                    <div class="empty-state">
                        <h4>🎁 No hay incentivos definidos</h4>
                        <p>Cree incentivos para motivar a los miembros del hogar</p>
                        <a href="${pageContext.request.contextPath}/incentivos?action=new" class="btn btn-primary">
                            ➕ Crear Primer Incentivo
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="incentivos-grid">
                        <c:forEach var="incentivoTipo" items="${listaIncentivosTipo}">
                            <div class="incentivo-card ${incentivoTipo.tipoIncentivo.toString().toLowerCase()}"
                                 data-tipo="${incentivoTipo.tipoIncentivo}">
                                
                                <div class="incentivo-header">
                                    <h4 class="incentivo-title">${incentivoTipo.nombre}</h4>
                                    <span class="incentivo-type">
                                        <c:choose>
                                            <c:when test="${incentivoTipo.tipoIncentivo == 'MONETARIO'}">💰</c:when>
                                            <c:when test="${incentivoTipo.tipoIncentivo == 'EXPERIENCIA'}">🎉</c:when>
                                            <c:when test="${incentivoTipo.tipoIncentivo == 'PRIVILEGIO'}">⭐</c:when>
                                            <c:when test="${incentivoTipo.tipoIncentivo == 'RECONOCIMIENTO'}">🏆</c:when>
                                            <c:otherwise>🎁</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>

                                <div class="incentivo-body">
                                    <c:if test="${not empty incentivoTipo.descripcion}">
                                        <p class="incentivo-description">
                                            ${fn:length(incentivoTipo.descripcion) > 100 ? 
                                              fn:substring(incentivoTipo.descripcion, 0, 100) += '...' : 
                                              incentivoTipo.descripcion}
                                        </p>
                                    </c:if>

                                    <div class="incentivo-meta">
                                        <div class="incentivo-tipo">
                                            <strong>Tipo:</strong> ${incentivoTipo.tipoIncentivo}
                                        </div>
                                        
                                        <c:if test="${not empty incentivoTipo.valor and incentivoTipo.valor > 0}">
                                            <div class="incentivo-valor">
                                                <strong>Puntos:</strong> ${incentivoTipo.valor}
                                            </div>
                                        </c:if>

                                        <div class="incentivo-status">
                                            <strong>Estado:</strong> 
                                            <c:choose>
                                                <c:when test="${incentivoTipo.disponible}">
                                                    <span class="status-available">✅ Disponible</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-unavailable">❌ No disponible</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <c:if test="${not empty incentivoTipo.limiteCantidad and incentivoTipo.limiteCantidad > 0}">
                                            <div class="incentivo-limite">
                                                <strong>Límite:</strong> ${incentivoTipo.limiteCantidad} disponibles
                                            </div>
                                        </c:if>
                                    </div>
                                </div>

                                <div class="incentivo-actions">
                                    <a href="${pageContext.request.contextPath}/incentivos?action=edit&id=${incentivoTipo.id}" 
                                       class="btn btn-outline btn-sm">
                                        ✏️ Editar
                                    </a>
                                    <a href="${pageContext.request.contextPath}/incentivos?action=toggle&id=${incentivoTipo.id}" 
                                       class="btn ${incentivoTipo.disponible ? 'btn-warning' : 'btn-success'} btn-sm">
                                        ${incentivoTipo.disponible ? '❌ Desactivar' : '✅ Activar'}
                                    </a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <!-- Historial de incentivos otorgados -->
        <c:if test="${not empty listaIncentivos}">
            <section class="incentivos-history">
                <h3>📚 Historial de Incentivos Otorgados</h3>
                
                <div class="history-table-container">
                    <table class="history-table">
                        <thead>
                            <tr>
                                <th>👤 Miembro</th>
                                <th>🎁 Tipo</th>
                                <th>📊 Puntos</th>
                                <th>📅 Fecha</th>
                                <th>📝 Descripción</th>
                                <th>📋 Tarea Relacionada</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="incentivo" items="${listaIncentivos}">
                                <tr class="history-row ${incentivo.tipoIncentivo.toString().toLowerCase()}">
                                    <td class="member-cell">
                                        <span class="member-name">${incentivo.miembroHogar.nombre}</span>
                                        <c:if test="${incentivo.miembroHogar.esJefe}">
                                            <span class="member-badge">👑</span>
                                        </c:if>
                                    </td>
                                    <td class="type-cell">
                                        <span class="incentivo-badge ${incentivo.tipoIncentivo.toString().toLowerCase()}">
                                            <c:choose>
                                                <c:when test="${incentivo.tipoIncentivo == 'RECOMPENSA'}">🏆 Recompensa</c:when>
                                                <c:when test="${incentivo.tipoIncentivo == 'PENALIZACION'}">⚠️ Penalización</c:when>
                                                <c:otherwise>${incentivo.tipoIncentivo}</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </td>
                                    <td class="points-cell">
                                        <span class="points-value ${incentivo.puntos > 0 ? 'positive' : 'negative'}">
                                            ${incentivo.puntos > 0 ? '+' : ''}${incentivo.puntos} pts
                                        </span>
                                    </td>
                                    <td class="date-cell">
                                        <fmt:formatDate value="${incentivo.fechaCreacion}" pattern="dd/MM/yyyy HH:mm" />
                                    </td>
                                    <td class="description-cell">
                                        <c:choose>
                                            <c:when test="${not empty incentivo.descripcion}">
                                                ${fn:length(incentivo.descripcion) > 50 ? 
                                                  fn:substring(incentivo.descripcion, 0, 50) += '...' : 
                                                  incentivo.descripcion}
                                            </c:when>
                                            <c:otherwise>
                                                <em>Sin descripción</em>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="task-cell">
                                        <c:choose>
                                            <c:when test="${not empty incentivo.quehacer}">
                                                <span class="task-link">${incentivo.quehacer.nombre}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <em>No relacionado</em>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <!-- Resumen estadístico del historial -->
                <div class="history-summary">
                    <h4>📈 Resumen Estadístico</h4>
                    <c:set var="totalRecompensas" value="0" />
                    <c:set var="totalPenalizaciones" value="0" />
                    <c:set var="puntosRecompensas" value="0" />
                    <c:set var="puntosPenalizaciones" value="0" />
                    
                    <c:forEach var="incentivo" items="${listaIncentivos}">
                        <c:choose>
                            <c:when test="${incentivo.tipoIncentivo == 'RECOMPENSA'}">
                                <c:set var="totalRecompensas" value="${totalRecompensas + 1}" />
                                <c:set var="puntosRecompensas" value="${puntosRecompensas + incentivo.puntos}" />
                            </c:when>
                            <c:when test="${incentivo.tipoIncentivo == 'PENALIZACION'}">
                                <c:set var="totalPenalizaciones" value="${totalPenalizaciones + 1}" />
                                <c:set var="puntosPenalizaciones" value="${puntosPenalizaciones + incentivo.puntos}" />
                            </c:when>
                        </c:choose>
                    </c:forEach>
                    
                    <div class="summary-grid">
                        <div class="summary-item success">
                            <strong>Recompensas:</strong> ${totalRecompensas}<br>
                            <strong>Puntos otorgados:</strong> 
                            <span class="points-positive">+${puntosRecompensas}</span>
                        </div>
                        <div class="summary-item warning">
                            <strong>Penalizaciones:</strong> ${totalPenalizaciones}<br>
                            <strong>Puntos descontados:</strong> 
                            <span class="points-negative">${puntosPenalizaciones}</span>
                        </div>
                        <div class="summary-item info">
                            <strong>Total incentivos:</strong> ${totalRecompensas + totalPenalizaciones}<br>
                            <strong>Balance neto:</strong> 
                            <c:set var="balanceNeto" value="${puntosRecompensas + puntosPenalizaciones}" />
                            <span class="balance-value ${balanceNeto >= 0 ? 'positive' : 'negative'}">
                                ${balanceNeto > 0 ? '+' : ''}${balanceNeto}
                            </span>
                        </div>
                    </div>
                </div>
            </section>
        </c:if>

        <!-- Información del sistema Observer -->
        <aside class="info-panel">
            <h3>🔔 Sistema de Notificaciones Activo</h3>
            <div class="observer-status">
                <div class="status-item">
                    <span class="status-icon">✅</span>
                    <span>Incentivos automáticos por tareas completadas</span>
                </div>
                <div class="status-item">
                    <span class="status-icon">⚠️</span>
                    <span>Penalizaciones por tareas vencidas</span>
                </div>
                <div class="status-item">
                    <span class="status-icon">🔄</span>
                    <span>Actualización automática de puntos</span>
                </div>
                <div class="status-item">
                    <span class="status-icon">📊</span>
                    <span>Registro de historial automático</span>
                </div>
            </div>
            <p><small>El sistema Observer gestiona automáticamente la asignación de incentivos y notificaciones.</small></p>
        </aside>
    </div>
</main>

<!-- JavaScript para filtros dinámicos -->
<script>
function filterIncentivos(tipo) {
    const cards = document.querySelectorAll('.incentivo-card');
    const buttons = document.querySelectorAll('.filter-btn');
    
    // Actualizar botones activos
    buttons.forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    
    // Filtrar tarjetas
    cards.forEach(card => {
        const cardTipo = card.dataset.tipo;
        const shouldShow = tipo === 'all' || cardTipo === tipo;
        card.style.display = shouldShow ? 'block' : 'none';
    });
}

// Inicializar componentes al cargar
document.addEventListener('DOMContentLoaded', function() {
    console.log('Vista de Incentivos cargada - Solo visualización de datos');
});
</script>

<jsp:include page="../common/footer.jsp" />
<jsp:include page="../common/layout-foot.jsp" />