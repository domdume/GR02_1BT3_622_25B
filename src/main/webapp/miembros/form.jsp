<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Registrar Miembro</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>Registrar Miembro</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/quehaceres">Volver al Tablero</a>
    </nav>
</header>
<div class="container">
    <form action="${pageContext.request.contextPath}/miembros" method="post">
        <input type="hidden" name="action" value="insert" />
        <fieldset>
            <label for="nombre">Nombre:</label>
            <input type="text" id="nombre" name="nombre" required />
        </fieldset>
        <fieldset>
            <label for="edad">Edad:</label>
            <input type="number" id="edad" name="edad" required />
        </fieldset>
        <button type="submit">Registrar Miembro</button>
    </form>
    <c:if test="${not empty successMessage}">
        <p style="color: green;">${successMessage}</p>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <p style="color: red;">${errorMessage}</p>
    </c:if>
    <h2>Miembros Registrados</h2>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Edad</th>
            <th>Puntos</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="miembro" items="${listaMiembros}">
            <tr>
                <td>${miembro.id}</td>
                <td>${miembro.nombre}</td>
                <td>${miembro.edad}</td>
                <td style="font-weight: bold; color: #2e7d32;">${miembro.puntos} pts</td>
            </tr>
        </c:forEach>
        <c:if test="${empty listaMiembros}">
            <tr>
                <td colspan="4">No hay miembros registrados.</td>
            </tr>
        </c:if>
        </tbody>
    </table>
</div>
</body>
</html>