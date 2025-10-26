<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Rachas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container">
    <h2>Rachas de Consistencia</h2>

    <c:if test="${not empty sessionScope.successMessage}">
        <div class="alert alert-success">${sessionScope.successMessage}</div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="alert alert-danger">${sessionScope.errorMessage}</div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <table class="table">
        <thead>
        <tr>
            <th>Miembro</th>
            <th>Puntos</th>
            <th>Racha actual (días)</th>
            <th>Acciones rápidas</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="m" items="${miembros}">
            <tr>
                <td>${m.nombre}</td>
                <td>${m.puntos}</td>
                <td>${rachaPorMiembro[m.id]}</td>
                <td>
                    <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/rachas?action=addToday&miembroId=${m.id}">+ Hoy</a>
                    <a class="btn btn-secondary btn-sm" href="${pageContext.request.contextPath}/rachas?action=addYesterday&miembroId=${m.id}">+ Ayer</a>
                    <a class="btn btn-secondary btn-sm" href="${pageContext.request.contextPath}/rachas?action=addTwoDaysAgo&miembroId=${m.id}">+ Anteayer</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <p class="muted">Reglas: la racha cuenta días consecutivos terminando en hoy o ayer. Varias tareas el mismo día cuentan como 1.</p>
</div>
<jsp:include page="/common/footer.jsp" />
</body>
</html>

