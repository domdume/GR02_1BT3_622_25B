<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Formulario de Miembro</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header>
        <div class="container">
            <div id="branding">
                <h1><c:if test="${miembro != null}">Editar</c:if><c:if test="${miembro == null}">Nuevo</c:if> Miembro</h1>
            </div>
            <nav>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/miembros">Volver a la Lista</a></li>
                </ul>
            </nav>
        </div>
    </header>
    <div class="container main">
        <form action="miembros?action=<c:if test="${miembro != null}">update</c:if><c:if test="${miembro == null}">insert</c:if>" method="post">
            <c:if test="${miembro != null}">
                <input type="hidden" name="id" value="<c:out value='${miembro.id}' />" />
            </c:if>

            <fieldset>
                <label for="nombre">Nombre:</label>
                <input type="text" id="nombre" name="nombre" value="<c:out value='${miembro.nombre}' />" required>
            </fieldset>

            <fieldset>
                <label for="edad">Edad:</label>
                <input type="number" id="edad" name="edad" value="<c:out value='${miembro.edad}' />" required min="0">
            </fieldset>

            <input type="submit" value="Guardar">
        </form>
    </div>
</body>
</html>