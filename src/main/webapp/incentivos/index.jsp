<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>Incentivos del Hogar</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>Historial de Incentivos</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/home">Volver al Tablero</a>
        <a href="${pageContext.request.contextPath}/miembros">Gestionar Miembros</a>
        <a href="${pageContext.request.contextPath}/quehaceres">Gestionar Quehaceres</a>
    </nav>
</header>
<div class="container">
    <!-- Mostrar mensajes del sistema -->
    <c:if test="${not empty successMessage}">
        <div style="background-color: #d4edda; border: 1px solid #c3e6cb; color: #155724; padding: 10px; margin: 10px 0; border-radius: 5px;">
            <strong>✅ Éxito:</strong> ${successMessage}
        </div>
        <c:set var="successMessage" value="" scope="session" />
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div style="background-color: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; padding: 10px; margin: 10px 0; border-radius: 5px;">
            <strong>❌ Error:</strong> ${errorMessage}
        </div>
        <c:set var="errorMessage" value="" scope="session" />
    </c:if>

    <h2>Incentivos Registrados</h2>
    
    <c:choose>
        <c:when test="${not empty listaIncentivos}">
            <table>
                <thead>
                    <tr>
                        <th>Miembro</th>
                        <th>Tipo de Incentivo</th>
                        <th>Puntos</th>
                        <th>Fecha</th>
                        <th>Descripción</th>
                        <th>Quehacer Relacionado</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="incentivo" items="${listaIncentivos}">
                        <tr>
                            <td>${incentivo.miembroHogar.nombre}</td>
                            <td>
                                <span class="badge ${incentivo.tipoIncentivo == 'RECOMPENSA' ? 'badge-success' : 'badge-warning'}">
                                    ${incentivo.tipoIncentivo}
                                </span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${incentivo.puntos > 0}">
                                        <span style="font-weight: bold; color: #2e7d32;">
                                            +${incentivo.puntos} pts
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="font-weight: bold; color: #d32f2f;">
                                            ${incentivo.puntos} pts
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <fmt:formatDate value="${incentivo.fechaCreacion}" pattern="dd/MM/yyyy HH:mm" />
                            </td>
                            <td>${incentivo.descripcion}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty incentivo.quehacer}">
                                        ${incentivo.quehacer.nombre}
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
            
            <!-- Resumen estadístico -->
            <div style="margin-top: 20px; padding: 15px; background-color: #f8f9fa; border-radius: 5px;">
                <h3>Resumen de Incentivos</h3>
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
                        <c:otherwise>
                            <c:set var="totalPenalizaciones" value="${totalPenalizaciones + 1}" />
                            <c:set var="puntosPenalizaciones" value="${puntosPenalizaciones + incentivo.puntos}" />
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                
                <div style="display: flex; gap: 20px;">
                    <div>
                        <strong>Recompensas:</strong> ${totalRecompensas}<br>
                        <strong>Puntos otorgados:</strong> <span style="color: #2e7d32;">+${puntosRecompensas}</span>
                    </div>
                    <div>
                        <strong>Penalizaciones:</strong> ${totalPenalizaciones}<br>
                        <strong>Puntos descontados:</strong> <span style="color: #d32f2f;">${puntosPenalizaciones}</span>
                    </div>
                    <div>
                        <strong>Total incentivos:</strong> ${totalRecompensas + totalPenalizaciones}<br>
                        <strong>Balance neto:</strong> 
                        <c:choose>
                            <c:when test="${puntosRecompensas + puntosPenalizaciones >= 0}">
                                <span style="color: #2e7d32;">
                                    ${puntosRecompensas + puntosPenalizaciones > 0 ? '+' : ''}${puntosRecompensas + puntosPenalizaciones}
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span style="color: #d32f2f;">
                                    ${puntosRecompensas + puntosPenalizaciones}
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div style="text-align: center; padding: 50px; background-color: #f8f9fa; border-radius: 5px;">
                <h3>No hay incentivos registrados</h3>
                <p>Los incentivos se generan automáticamente cuando los miembros completan o no cumplen con sus quehaceres.</p>
                <a href="${pageContext.request.contextPath}/quehaceres?action=new" class="button">Crear Primer Quehacer</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<style>
.badge {
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 0.75em;
    font-weight: bold;
    text-transform: uppercase;
}
.badge-success {
    background-color: #d4edda;
    color: #155724;
    border: 1px solid #c3e6cb;
}
.badge-warning {
    background-color: #fff3cd;
    color: #856404;
    border: 1px solid #ffeaa7;
}
</style>
</body>
</html>