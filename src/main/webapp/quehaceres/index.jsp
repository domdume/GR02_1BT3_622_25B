<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Gestionar Quehaceres</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header>
        <div class="container">
            <div id="branding">
                <h1><span class="highlight">Gestión</span> de Quehaceres</h1>
            </div>
            <nav>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/">Inicio</a></li>
                </ul>
            </nav>
        </div>
    </header>

    <div class="container main">
        <a href="quehaceres?action=new" class="button">Añadir Nuevo Quehacer</a>
        <br><br>

        <h2>Lista de Quehaceres</h2>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Descripción</th>
                    <th>Dificultad</th>
                    <th>Asignado a</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="quehacer" items="${listaQuehaceres}">
                    <tr>
                        <td><c:out value="${quehacer.id}" /></td>
                        <td><c:out value="${quehacer.nombre}" /></td>
                        <td><c:out value="${quehacer.dificultad}" /></td>
                        <td><c:out value="${quehacer.miembroHogar.nombre}" /></td>
                        <td>
                            <a href="quehaceres?action=edit&id=<c:out value='${quehacer.id}' />">Editar</a>
                            &nbsp;&nbsp;&nbsp;&nbsp;
                            <a href="quehaceres?action=delete&id=<c:out value='${quehacer.id}' />" onclick="return confirm('¿Estás seguro de que quieres eliminar este quehacer?');">Eliminar</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</body>
</html>