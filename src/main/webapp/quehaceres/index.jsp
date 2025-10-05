<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Gestión de Quehaceres</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>Gestión de Quehaceres</h1>
</header>
<div class="container">
    <a href="quehaceres?action=new" class="btn btn-primary">Añadir Nuevo Quehacer</a>
    <table>
        <thead>
            <tr>
                <th>Nombre del Quehacer</th>
                <th>Asignado a</th>
                <th>Puntos del Miembro</th>
                <th>Fecha Límite</th>
                <th>Estado</th>
                <th>Recompensa / Penalización</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="q" items="${listaQuehaceres}">
                <tr>
                    <td>${q.nombre}</td>
                    <td>${q.miembroHogar.nombre}</td>
                    <td style="font-weight: bold; color: #2e7d32;">${q.miembroHogar.puntos} pts</td>
                    <td>${q.tiempoLimite}</td>
                    <td>${q.estadoFinalizado ? 'Finalizado' : 'Pendiente'}</td>
                    <td>${q.estadoFinalizado ? (q.estadoCompletado ? q.recompensa : q.penalizacion) : 'Sin asignar'}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/quehaceres?action=edit&id=${q.id}" class="btn btn-warning">Editar</a>
                        <a href="${pageContext.request.contextPath}/quehaceres?action=delete&id=${q.id}" class="btn btn-danger" onclick="return confirm('¿Estás seguro de eliminar este quehacer?');">Eliminar</a>
                        <a href="${pageContext.request.contextPath}/quehaceres?action=complete" class="btn btn-success">Ir a Completar Quehaceres</a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty listaQuehaceres}">
                <tr>
                    <td colspan="6">No hay quehaceres registrados.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>
</body>
</html>