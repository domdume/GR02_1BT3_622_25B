<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Formulario de Quehacer</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header>
        <div class="container">
            <div id="branding">
                <h1><c:if test="${quehacer != null}">Editar</c:if><c:if test="${quehacer == null}">Nuevo</c:if> Quehacer</h1>
            </div>
            <nav>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/quehaceres">Volver a la Lista</a></li>
                </ul>
            </nav>
        </div>
    </header>
    <div class="container main">
        <form action="quehaceres?action=<c:if test="${quehacer != null}">update</c:if><c:if test="${quehacer == null}">insert</c:if>" method="post">
            <c:if test="${quehacer != null}">
                <input type="hidden" name="id" value="<c:out value='${quehacer.id}' />" />
            </c:if>

            <fieldset>
                <label for="nombre">Descripción:</label>
                <input type="text" id="nombre" name="nombre" value="<c:out value='${quehacer.nombre}' />" required>
            </fieldset>

            <fieldset>
                <label for="dificultad">Dificultad:</label>
                <select id="dificultad" name="dificultad" required>
                    <c:forEach var="d" items="${dificultades}">
                        <option value="${d}" ${quehacer.dificultad == d ? 'selected' : ''}>${d}</option>
                    </c:forEach>
                </select>
            </fieldset>

            <fieldset>
                <label for="tiempoLimite">Fecha Límite:</label>
                <input type="datetime-local" id="tiempoLimite" name="tiempoLimite" value="${quehacer.tiempoLimite}" required>
            </fieldset>

            <fieldset>
                <label for="miembroId">Asignar a:</label>
                <select id="miembroId" name="miembroId" required>
                    <c:forEach var="miembro" items="${listaMiembros}">
                        <option value="${miembro.id}" ${quehacer.miembroHogar.id == miembro.id ? 'selected' : ''}>${miembro.nombre}</option>
                    </c:forEach>
                </select>
            </fieldset>

            <input type="submit" value="Guardar">
        </form>
    </div>
</body>
</html>