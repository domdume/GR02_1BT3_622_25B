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
                        <th>Recompensa / Penalización</th>
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
                            <td>
                                <div style="text-align: center;">
                                    <strong style="color: #2e7d32; font-size: 1.1em;">${quehacer.puntosEnEseMomento} pts</strong><br>
                                    <small style="color: #666;">
                                        <c:choose>
                                            <c:when test="${quehacer.estadoFinalizado}">
                                                Total después de esta tarea
                                            </c:when>
                                            <c:otherwise>
                                                Total actual
                                            </c:otherwise>
                                        </c:choose>
                                    </small>
                                </div>
                            </td>
                            <td>
                                <strong>${quehacer.tiempoLimite}</strong>
                                <c:if test="${quehacer.estadoFinalizado and quehacer.fechaFinalizacion != null}">
                                    <br><small style="color: #666;">
                                        <c:choose>
                                            <c:when test="${quehacer.estadoCompletado}">
                                                Completado: ${quehacer.fechaFinalizacion}
                                            </c:when>
                                            <c:otherwise>
                                                Expiró: ${quehacer.fechaFinalizacion}
                                            </c:otherwise>
                                        </c:choose>
                                    </small>
                                </c:if>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${quehacer.estadoFinalizado}">
                                        <c:choose>
                                            <c:when test="${quehacer.estadoCompletado}">
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
                                    <c:when test="${quehacer.estadoFinalizado}">
                                        <c:choose>
                                            <c:when test="${quehacer.estadoCompletado}">
                                                <span style="color: #000; font-weight: normal;" class="recompensa-text" data-quehacer-id="${quehacer.id}">Cargando...</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: #000; font-weight: normal;" class="penalizacion-text" data-quehacer-id="${quehacer.id}">Cargando...</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: #666;">Pendiente de completar</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
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

<script>
// Listas de recompensas y penalizaciones divertidas
const recompensas = [
    "No debes lavar los platos durante una semana",
    "Libre de hacer tu cama por 3 días",
    "Puedes elegir la película de la noche",
    "No tienes que sacar la basura esta semana",
    "Día libre de cocinar",
    "Puedes quedarte despierto 1 hora extra",
    "Libre de aspirar por una semana",
    "Eliges el menú del domingo",
    "No haces limpieza del baño por 5 días",
    "Tienes el control remoto por un día completo"
];

const penalizaciones = [
    "Debes barrer durante una semana",
    "Lavar los platos todos los días por 3 días",
    "Sacar la basura durante una semana completa",
    "Limpiar el baño por 5 días seguidos",
    "Aspirar toda la casa durante una semana",
    "Hacer todas las camas por 3 días",
    "Limpiar las ventanas de toda la casa",
    "Ordenar tu cuarto todos los días por una semana",
    "Lavar y doblar ropa por 5 días",
    "Cocinar la cena durante una semana"
];

// Función para obtener un elemento aleatorio de una lista basado en un ID (para consistencia)
function getConsistentRandomItem(array, id) {
    // Usar el ID como semilla para obtener siempre el mismo resultado para el mismo quehacer
    const index = Math.abs(id) % array.length;
    return array[index];
}

// Asignar recompensas y penalizaciones cuando se carga la página
document.addEventListener('DOMContentLoaded', function() {
    // Asignar recompensas
    document.querySelectorAll('.recompensa-text').forEach(function(element) {
        const quehacerId = element.getAttribute('data-quehacer-id');
        const recompensa = getConsistentRandomItem(recompensas, parseInt(quehacerId));
        element.innerHTML = recompensa;
    });
    
    // Asignar penalizaciones
    document.querySelectorAll('.penalizacion-text').forEach(function(element) {
        const quehacerId = element.getAttribute('data-quehacer-id');
        const penalizacion = getConsistentRandomItem(penalizaciones, parseInt(quehacerId));
        element.innerHTML = penalizacion;
    });
});
</script>

</body>
</html>