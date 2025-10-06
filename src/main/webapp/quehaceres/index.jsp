<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Gestión de Quehaceres</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>Gestión de Quehaceres</h1>
</header>
<div class="container">
    <a href="quehaceres?action=new" class="btn btn-primary">Añadir Nuevo Quehacer</a>
    <table>
        <thead>
            <tr>
                <th>Nombre del Quehacer</th>
                <th>Asignado a</th>
                <th>Puntos del Miembro</th>
                <th>Fecha Límite</th>
                <th>Estado</th>
                <th>Recompensa / Penalización</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="q" items="${listaQuehaceres}">
                <tr>
                    <td>${q.nombre}</td>
                    <td>${q.miembroHogar.nombre}</td>
                    <td>
                        <div style="text-align: center;">
                            <strong style="color: #2e7d32; font-size: 1.1em;">${q.puntosEnEseMomento} pts</strong><br>
                            <small style="color: #666;">
                                <c:choose>
                                    <c:when test="${q.estadoFinalizado}">
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
                        <strong>${q.tiempoLimite}</strong>
                        <c:if test="${q.estadoFinalizado and q.fechaFinalizacion != null}">
                            <br><small style="color: #666;">
                                <c:choose>
                                    <c:when test="${q.estadoCompletado}">
                                        Completado: ${q.fechaFinalizacion}
                                    </c:when>
                                    <c:otherwise>
                                        Expiró: ${q.fechaFinalizacion}
                                    </c:otherwise>
                                </c:choose>
                            </small>
                        </c:if>
                    </td>
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
                                        <span style="color: #000; font-weight: normal;" class="recompensa-text" data-quehacer-id="${q.id}">Cargando...</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: #000; font-weight: normal;" class="penalizacion-text" data-quehacer-id="${q.id}">Cargando...</span>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <span style="color: #666;">Pendiente de completar</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/quehaceres?action=delete&id=${q.id}" class="btn btn-danger" onclick="return confirm('¿Estás seguro de eliminar este quehacer?');">Eliminar</a>
                        <a href="${pageContext.request.contextPath}/quehaceres?action=complete" class="btn btn-success">Ir a Completar Quehaceres</a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty listaQuehaceres}">
                <tr>
                    <td colspan="7">No hay quehaceres registrados.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
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