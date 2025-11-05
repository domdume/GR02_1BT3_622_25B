<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${not empty sessionScope.achievementMessage}">
    <style>
        .achievement-toast {
            position: fixed;
            right: 20px;
            bottom: 20px;
            background: #fff;
            border: 1px solid #e0e0e0;
            box-shadow: 0 6px 18px rgba(0,0,0,0.12);
            border-radius: 10px;
            padding: 12px 16px;
            display: flex;
            align-items: center;
            gap: 12px;
            z-index: 1000;
            max-width: 360px;
        }
        .achievement-icon { font-size: 24px; }
        .achievement-content { flex: 1; }
        .achievement-title { font-weight: 600; margin-bottom: 4px; }
        .achievement-message { font-size: 14px; color: #333; }
        .achievement-close {
            border: none;
            background: transparent;
            font-size: 16px;
            cursor: pointer;
            color: #666;
        }
        .achievement-close:hover { color: #000; }
    </style>
    <div class="achievement-toast">
        <div class="achievement-icon">🏆</div>
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
        // Auto ocultar después de 9s
        setTimeout(function(){
            var el = document.querySelector('.achievement-toast');
            if (el) el.remove();
        }, 9000);
    </script>
    <c:remove var="achievementMessage" scope="session"/>
    <c:remove var="achievementLogroId" scope="session"/>
</c:if>
