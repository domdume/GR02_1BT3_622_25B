<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${not empty sessionScope.achievementMessage && sessionScope.achievementTipo == 'LOGRO_RACHA'}">
    <div class="achievement-toast">
        <div class="achievement-icon">🔥</div>
        <div class="achievement-content">
            <div class="achievement-title">
                <c:choose>
                    <c:when test="${sessionScope.achievementLogroId == 'LOGRO_7'}">Racha de 7 días</c:when>
                    <c:otherwise>Racha de 3 días</c:otherwise>
                </c:choose>
            </div>
            <div class="achievement-message">${sessionScope.achievementMessage}</div>
        </div>
        <button class="achievement-close" onclick="this.parentElement.remove()">✖</button>
    </div>
    <script>
        // Auto ocultar después de 6s
        setTimeout(function(){
            var el = document.querySelector('.achievement-toast');
            if (el) el.remove();
        }, 6000);
    </script>
    <c:remove var="achievementMessage" scope="session"/>
    <c:remove var="achievementLogroId" scope="session"/>
    <c:remove var="achievementTipo" scope="session"/>
</c:if>
