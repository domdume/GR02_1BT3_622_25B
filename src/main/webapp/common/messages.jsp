<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Mostrar mensajes del sistema de forma centralizada -->
<c:if test="${not empty successMessage}">
    <div class="alert alert-success">
        <strong>✅ Éxito:</strong> ${successMessage}
    </div>
</c:if>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-error">
        <strong>❌ Error:</strong> ${errorMessage}
    </div>
</c:if>

<c:if test="${not empty infoMessage}">
    <div class="alert alert-info">
        <strong>ℹ️ Información:</strong> ${infoMessage}
    </div>
</c:if>

<c:if test="${not empty mensaje}">
    <div class="alert alert-system">
        <strong>🏗️ Sistema:</strong> ${mensaje}
    </div>
</c:if>
