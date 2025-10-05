<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Registrar Quehacer Completado</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>Registrar Quehacer Completado</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/quehaceres">Volver al Tablero</a>
    </nav>
</header>
<div class="container">
    <form action="quehaceres" method="post">
        <input type="hidden" name="action" value="markComplete" />
        <fieldset>
            <label for="quehacerId">Seleccionar Quehacer:</label>
            <select id="quehacerId" name="quehacerId" required onchange="updateMiembroAsignado()">
                <option value="">-- Selecciona un quehacer --</option>
                <c:forEach var="quehacer" items="${listaQuehaceres}">
                    <option value="${quehacer.id}" data-miembro="${quehacer.miembroHogar.nombre}">
                        ${quehacer.nombre} - Asignado a: ${quehacer.miembroHogar.nombre}
                    </option>
                </c:forEach>
            </select>
        </fieldset>
        <fieldset>
            <label for="miembroAsignado">Miembro Asignado:</label>
            <input type="text" id="miembroAsignado" name="miembroAsignado" readonly 
                   style="background-color: #f9f9f9; border: 2px solid #4CAF50; padding: 8px; border-radius: 4px; color: #333; font-weight: bold;" 
                   placeholder="Selecciona un quehacer para ver el miembro asignado" />
            <small style="color: #666; font-style: italic;">Este campo se actualiza automáticamente según el quehacer seleccionado</small>
        </fieldset>
        <fieldset>
            <label for="fechaFinalizacion">Fecha de Finalización:</label>
            <input type="datetime-local" id="fechaFinalizacion" name="fechaFinalizacion" required />
        </fieldset>
        <button type="submit">Tarea Terminada</button>
    </form>
    
    <c:if test="${not empty successMessage}">
        <p style="color: green;">${successMessage}</p>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <p style="color: red;">${errorMessage}</p>
    </c:if>
    
    <c:if test="${empty listaQuehaceres}">
        <p style="color: orange;">No hay quehaceres pendientes para completar.</p>
    </c:if>
</div>

<script>
function updateMiembroAsignado() {
    const quehacerSelect = document.getElementById('quehacerId');
    const miembroInput = document.getElementById('miembroAsignado');
    
    const selectedOption = quehacerSelect.options[quehacerSelect.selectedIndex];
    
    if (selectedOption && selectedOption.value !== '') {
        const miembroNombre = selectedOption.getAttribute('data-miembro');
        miembroInput.value = miembroNombre;
    } else {
        miembroInput.value = '';
    }
}

// Inicializar cuando se carga la página
document.addEventListener('DOMContentLoaded', function() {
    updateMiembroAsignado();
});
</script>

</body>
</html>
