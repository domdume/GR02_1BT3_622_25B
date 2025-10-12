<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<header class="main-header">
    <div class="container">
        <div class="header-content">
            <h1 class="site-title">
                <span class="highlight">Sistema</span> de Gestión del Hogar
            </h1>
            <nav class="main-navigation">
                <ul>
                    <li><a href="${pageContext.request.contextPath}/home" 
                           class="${pageContext.request.requestURI.contains('/home') ? 'active' : ''}">
                        🏠 Dashboard</a></li>
                    <li><a href="${pageContext.request.contextPath}/miembros" 
                           class="${pageContext.request.requestURI.contains('/miembros') ? 'active' : ''}">
                        👥 Miembros</a></li>
                    <li><a href="${pageContext.request.contextPath}/quehaceres" 
                           class="${pageContext.request.requestURI.contains('/quehaceres') ? 'active' : ''}">
                        📋 Quehaceres</a></li>
                    <li><a href="${pageContext.request.contextPath}/incentivos" 
                           class="${pageContext.request.requestURI.contains('/incentivos') ? 'active' : ''}">
                        🏆 Incentivos</a></li>
                </ul>
            </nav>
        </div>
    </div>
</header>