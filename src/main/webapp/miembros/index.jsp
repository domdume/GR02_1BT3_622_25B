<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Gestionar Miembros del Hogar</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header>
        <div class="container">
            <div id="branding">
                <h1><span class="highlight">Gestión</span> de Miembros del Hogar</h1>
            </div>
            <nav>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/">Inicio</a></li>
                </ul>
            </nav>
        </div>
    </header>

    <div class="container main">
        <a href="miembros?action=new" class="button">Añadir Nuevo Miembro</a>
        <br><br>

        <h2>Lista de Miembros</h2>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Edad</th>
                    <th>Puntos</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="miembro" items="${listaMiembros}">
                    <tr>
                        <td><c:out value="${miembro.id}" /></td>
                        <td><c:out value="${miembro.nombre}" /></td>
                        <td><c:out value="${miembro.edad}" /></td>
                        <td style="font-weight: bold; color: #2e7d32;"><c:out value="${miembro.puntos}" /> pts</td>
                        <td>
                            <a href="miembros?action=edit&id=<c:out value='${miembro.id}' />">Editar</a>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            <a href="miembros?action=delete&id=<c:out value='${miembro.id}' />" onclick="return confirm('¿Estás seguro de que quieres eliminar a este miembro?');">Eliminar</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty listaMiembros}">
                    <tr>
                        <td colspan="5">No hay miembros registrados.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</body>
</html>