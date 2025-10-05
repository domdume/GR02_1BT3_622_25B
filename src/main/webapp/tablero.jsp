<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Tablero del Hogar</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>Tablero del Hogar</h1>
    <nav>
        <!-- Funcionalidades del Jefe del Hogar (InterfazDelJefe) -->
        <a href="${pageContext.request.contextPath}/miembros?action=new">Registrar Miembro</a>
        <a href="${pageContext.request.contextPath}/quehaceres?action=new">Establecer Quehacer</a>
        
        <!-- Funcionalidades del Miembro Familia (InterfazDelMiembroFamilia) -->
        <a href="${pageContext.request.contextPath}/quehaceres?action=complete">Completar Quehacer</a>
        <a href="${pageContext.request.contextPath}/quehaceres?action=pending">Revisar Quehaceres Pendientes</a>
    </nav>
</header>
<div class="container">
    <!-- Mostrar mensajes del sistema -->
    <c:if test="${not empty successMessage}">
        <div style="background-color: #d4edda; border: 1px solid #c3e6cb; color: #155724; padding: 10px; margin: 10px 0; border-radius: 5px;">
            <strong>✅ Éxito:</strong> ${successMessage}
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div style="background-color: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; padding: 10px; margin: 10px 0; border-radius: 5px;">
            <strong>❌ Error:</strong> ${errorMessage}
        </div>
    </c:if>
    <c:if test="${not empty mensaje}">
        <div style="background-color: #e8f5e8; border: 1px solid #4caf50; padding: 10px; margin: 10px 0; border-radius: 5px;">
            <strong>🏗️ Sistema UML:</strong> ${mensaje}
        </div>
    </c:if>

    <h2>Quehaceres del Hogar</h2>
    
    <c:choose>
        <c:when test="${not empty listaQuehaceres}">
            <table>
                <thead>
                    <tr>
                        <th>Nombre del Quehacer</th>
                        <th>Asignado a</th>
                        <th>Puntos del Miembro</th>
                        <th>Fecha Límite</th>
                        <th>Estado</th>
                        <th>Recompensa / Penalización</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="q" items="${listaQuehaceres}">
                        <tr class="${q.estadoFinalizado ? (q.estadoCompletado ? 'completed' : 'overdue') : ''}">
                            <td>${q.nombre}</td>
                            <td>${q.miembroHogar.nombre}</td>
                            <td style="font-weight: bold; color: #2e7d32;">${q.miembroHogar.puntos} pts</td>
                            <td>${q.tiempoLimite}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${q.estadoFinalizado}">
                                        <c:choose>
                                            <c:when test="${q.estadoCompletado}">
                                                <span style="color: #4caf50; font-weight: bold;">✅ Completado</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: #f44336; font-weight: bold;">❌ Atrasado</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: #ff9800; font-weight: bold;">⏳ Pendiente</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${q.estadoFinalizado}">
                                        <c:choose>
                                            <c:when test="${q.estadoCompletado}">
                                                <span style="color: #4caf50;">${q.recompensa}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: #f44336;">${q.penalizacion}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: #666;">Sin asignar</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div style="text-align: center; padding: 40px; background-color: #f5f5f5; border-radius: 8px; margin: 20px 0;">
                <h3 style="color: #666;">📋 No hay quehaceres registrados</h3>
                <p style="color: #888;">Comienza registrando miembros de la familia y asignando quehaceres.</p>
                <a href="${pageContext.request.contextPath}/miembros?action=new" style="background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin: 10px;">Registrar Miembro</a>
                <a href="${pageContext.request.contextPath}/quehaceres?action=new" style="background-color: #2196F3; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin: 10px;">Crear Quehacer</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>