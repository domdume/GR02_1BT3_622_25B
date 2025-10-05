<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Establecer Quehacer</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>Establecer Quehacer</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/quehaceres">Volver al Tablero</a>
    </nav>
</header>
<div class="container">
    
    <form action="quehaceres" method="post">
        <input type="hidden" name="action" value="insert" />
        <fieldset>
            <label for="nombre">Nombre del Quehacer:</label>
            <input type="text" id="nombre" name="nombre" required />
        </fieldset>
        <fieldset>
            <label for="tiempoLimite">Fecha Límite:</label>
            <input type="datetime-local" id="tiempoLimite" name="tiempoLimite" required />
        </fieldset>
        <fieldset>
            <label for="miembroId">Asignado a:</label>
            <select id="miembroId" name="miembroId" required>
                <c:forEach var="miembro" items="${listaMiembros}">
                    <option value="${miembro.id}">${miembro.nombre}</option>
                </c:forEach>
                <c:if test="${empty listaMiembros}">
                    <option disabled>No hay miembros disponibles</option>
                </c:if>
            </select>
        </fieldset>
        <fieldset>
            <label for="dificultad">Dificultad:</label>
            <select id="dificultad" name="dificultad" required>
                <option value="FACIL">Fácil</option>
                <option value="MEDIO" selected>Medio</option>
                <option value="DIFICIL">Difícil</option>
            </select>
        </fieldset>
        <button type="submit">Agregar Quehacer</button>
    </form>
    <c:if test="${not empty errorMessage}">
        <p style="color: red;">${errorMessage}</p>
    </c:if>

    <!-- Tabla de Quehaceres Existentes -->
    <h2>Quehaceres Registrados</h2>
    <c:choose>
        <c:when test="${not empty listaQuehaceres}">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Dificultad</th>
                        <th>Asignado a</th>
                        <th>Puntos</th>
                        <th>Fecha Límite</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="quehacer" items="${listaQuehaceres}">
                        <tr>
                            <td>${quehacer.id}</td>
                            <td>${quehacer.nombre}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${quehacer.dificultad == 'FACIL'}">
                                        <span style="color: green; font-weight: bold;">🟢 Fácil</span>
                                    </c:when>
                                    <c:when test="${quehacer.dificultad == 'MEDIO'}">
                                        <span style="color: orange; font-weight: bold;">🟡 Medio</span>
                                    </c:when>
                                    <c:when test="${quehacer.dificultad == 'DIFICIL'}">
                                        <span style="color: red; font-weight: bold;">🔴 Difícil</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: gray;">❔ No definida</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${quehacer.miembroHogar.nombre}</td>
                            <td>${quehacer.miembroHogar.puntos}</td>
                            <td>${quehacer.tiempoLimite}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${quehacer.estadoCompletado}">
                                        <span style="color: green;">✓ Completado</span>
                                    </c:when>
                                    <c:when test="${quehacer.estadoFinalizado}">
                                        <span style="color: red;">✗ Finalizado (Vencido)</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: orange;">⏳ Pendiente</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a href="quehaceres?action=edit&id=${quehacer.id}">Editar</a>
                                <a href="quehaceres?action=delete&id=${quehacer.id}" 
                                   onclick="return confirm('¿Está seguro de eliminar este quehacer?')">Eliminar</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <p>No hay quehaceres registrados.</p>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>