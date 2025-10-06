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