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
    <div class="table-responsive">
    <table class="data-table">
        <thead>
        <tr>
            <th>Miembro</th>
            <th>Puntos</th>
            <th>Racha actual</th>
            <th>Estado</th>
            <th>Simular Tarea (Demo)</th>
            <th>Acciones Jefe</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="m" items="${miembros}">
            <tr>
                <td>${m.nombre}</td>
                <td>${m.puntos}</td>
                <td>
                    <span class="points-badge">${rachaPorMiembro[m.id]}<span style="margin-left:6px;">día(s)</span></span>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${m.rachaCongelada}"><span class="status-badge completed">❄️ Protegida</span></c:when>
                        <c:otherwise><span class="status-badge pending">Activa</span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/rachas?action=addToday&miembroId=${m.id}">+ Hoy</a>
                    <a class="btn btn-secondary btn-sm" href="${pageContext.request.contextPath}/rachas?action=addYesterday&miembroId=${m.id}">+ Ayer</a>
                    <a class="btn btn-secondary btn-sm" href="${pageContext.request.contextPath}/rachas?action=addTwoDaysAgo&miembroId=${m.id}">+ Anteayer</a>
                    <a class="btn btn-success btn-sm" href="${pageContext.request.contextPath}/logros/racha/verify?miembroId=${m.id}">Verificar Logro de Racha</a>
                </td>
                <td>
                    <c:if test="${sessionScope.viewRole == 'JEFE'}">
                        <form method="post" action="${pageContext.request.contextPath}/miembros/congelar" style="display:inline-flex; align-items:center; gap:8px;">
                            <input type="hidden" name="miembroId" value="${m.id}"/>
                            <input type="hidden" name="freeze" value="${!m.rachaCongelada}"/>
                            <button type="submit" class="btn btn-outline btn-sm">
                                <c:choose>
                                    <c:when test="${m.rachaCongelada}">Quitar Protección</c:when>
                                    <c:otherwise>Proteger Racha ❄️</c:otherwise>
                                </c:choose>
                            </button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </div>

    <p class="muted">Reglas: la racha cuenta días consecutivos terminando en hoy o ayer. Varias tareas el mismo día cuentan como 1. Cuando la racha está protegida (❄️), se puede ignorar un día faltante al calcular la racha.</p>
</div>
<jsp:include page="/common/achievement-toast.jsp" />
<jsp:include page="/common/footer.jsp" />
</body>
</html>
