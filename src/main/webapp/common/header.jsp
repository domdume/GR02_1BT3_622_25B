<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<header class="main-header">
    <div class="container">
        <div class="header-content">
            <h1 class="site-title">
                <span class="highlight">ChoresFun</span> Gestión del Hogar
            </h1>
            <nav class="main-navigation">
                <ul>
                    <li><a href="${pageContext.request.contextPath}/home" 
                           class="${pageContext.request.requestURI.contains('/home') ? 'active' : ''}">
                        🏠 Inicio</a></li>
                    <li><a href="${pageContext.request.contextPath}/miembros" 
                           class="${pageContext.request.requestURI.contains('/miembros') ? 'active' : ''}">
                        👥 Miembros</a></li>
                    <li><a href="${pageContext.request.contextPath}/quehaceres?action=listGestion"
                           class="${pageContext.request.requestURI.contains('/quehaceres') ? 'active' : ''}">
                        📋 Quehaceres</a></li>
                    <li><a href="${pageContext.request.contextPath}/rachas"
                           class="${pageContext.request.requestURI.contains('/rachas') ? 'active' : ''}">🔥 Rachas</a></li>
                    <li><a href="${pageContext.request.contextPath}/incentivos"
                           class="${pageContext.request.requestURI.contains('/incentivos') ? 'active' : ''}">
                        🏆 Incentivos</a></li>
                    <li><a href="${pageContext.request.contextPath}/ranking"
                           class="${pageContext.request.requestURI.contains('/ranking') ? 'active' : ''}">🏆 Ranking</a></li>
                </ul>
            </nav>
        </div>
        <div class="role-switcher">
            <span class="role-indicator">
                👤 Viendo como:
                <strong>
                    <c:choose>
                        <c:when test="${sessionScope.viewRole == 'JEFE'}">Jefe del Hogar</c:when>
                        <c:otherwise>Miembro del Hogar</c:otherwise>
                    </c:choose>
                </strong>
            </span>
            <div class="role-actions">
                <a class="btn btn-secondary btn-sm ${sessionScope.viewRole != 'JEFE' ? 'active' : ''}" href="${pageContext.request.contextPath}/viewas?role=MIEMBRO">Ver como Miembro</a>
                <a class="btn btn-secondary btn-sm ${sessionScope.viewRole == 'JEFE' ? 'active' : ''}" href="${pageContext.request.contextPath}/viewas?role=JEFE">Ver como Jefe</a>
            </div>
        </div>
    </div>
</header>