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

    <%-- Mensajes de éxito/error --%>
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="alert alert-success">${sessionScope.successMessage}</div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="alert alert-danger">${sessionScope.errorMessage}</div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <div class="alert alert-info" role="alert" style="margin-top: 15px; margin-bottom: 15px;">
        <strong>Nota importante para probar la funcionalidad de la racha:</strong>
        <p>
            Para probar la funcionalidad de las rachas, utilice los botones de la columna <strong>"Simular Tarea"</strong>.
            Estos botones registran una tarea completada ficticia en el día indicado (Hoy, Ayer, Anteayer) y recalcularán
            la racha del miembro automáticamente.
        </p>
        <p class="mb-0">
            Ejemplo: Para probar una racha de 2 días, haga clic en "+ Ayer" y luego en "+ Hoy".
            Tomar en cuenta que este demo indica solo hasta 3 días de racha.
        </p>
    </div>
    <table class="table">
        <thead>
        <tr>
            <th>Miembro</th>
            <th>Puntos</th>
            <th>Racha actual (días)</th>
            <th>Simular Tarea (Demo)</th>
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
</body>
</html>