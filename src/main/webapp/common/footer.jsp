<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<footer class="main-footer">
    <div class="container">
        <div class="footer-content">
            <p>&copy; 2025 Sistema de Gestión del Hogar | 
               <span class="highlight">Patrón Observer</span> Implementado</p>
            <div class="footer-stats">
                <c:if test="${not empty estadisticas}">
                    <span>👥 ${estadisticas.totalMiembros} miembros</span>
                    <span>📋 ${estadisticas.totalQuehaceres} tareas</span>
                    <span>🏆 ${estadisticas.totalIncentivos} incentivos</span>
                </c:if>
            </div>
        </div>
    </div>
</footer>