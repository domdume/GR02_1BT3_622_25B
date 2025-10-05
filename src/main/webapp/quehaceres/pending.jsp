<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Revisar Quehaceres Pendientes</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>Revisar Quehaceres Pendientes</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/quehaceres">Volver al Tablero</a>
    </nav>
</header>
<div class="container">
    <form action="quehaceres" method="get">
        <input type="hidden" name="action" value="pending" />
        <fieldset>
            <label for="miembroId">Seleccionar Miembro:</label>
            <select id="miembroId" name="miembroId" required>
                <option value="">-- Seleccione un miembro --</option>
                <c:forEach var="miembro" items="${listaMiembros}">
                    <option value="${miembro.id}" 
                            <c:if test="${miembro.id == miembroSeleccionado}">selected</c:if>>
                        ${miembro.nombre}
                    </option>
                </c:forEach>
                <c:if test="${empty listaMiembros}">
                    <option disabled>No hay miembros registrados</option>
                </c:if>
            </select>
        </fieldset>
        <button type="submit">Ver Tareas Pendientes</button>
    </form>
    <h2>Tareas Pendientes</h2>
    
    <c:if test="${not empty miembroSeleccionado and not empty listaMiembros}">
        <c:forEach var="miembro" items="${listaMiembros}">
            <c:if test="${miembro.id == miembroSeleccionado}">
                <p><strong>Mostrando tareas pendientes de: ${miembro.nombre}</strong></p>
            </c:if>
        </c:forEach>
    </c:if>
    
    <c:choose>
        <c:when test="${empty miembroSeleccionado}">
            <p style="color: #666;">Seleccione un miembro para ver sus tareas pendientes.</p>
        </c:when>
        <c:when test="${empty tareasPendientes}">
            <p style="color: green;">¡Felicidades! No hay tareas pendientes para este miembro.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>Quehacer</th>
                        <th>Fecha Límite</th>
                        <th>Estado</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="quehacer" items="${tareasPendientes}">
                        <tr>
                            <td>${quehacer.nombre}</td>
                            <td>${quehacer.tiempoLimite}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${quehacer.overdue}">
                                        <span style="color: red;">Vencida</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: orange;">Pendiente</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>