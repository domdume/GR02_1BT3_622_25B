<%-- 
    Vista de Completar Quehaceres - Solo formulario de entrada
    Responsabilidad: Capturar datos de finalización y enviar al servlet
    Sin lógica de negocio - Solo vista pura MVC
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="Completar Tarea" scope="request" />
<c:set var="bodyClass" value="complete-task-page" scope="request" />

<jsp:include page="../common/layout-head.jsp" />
<jsp:include page="../common/header.jsp" />

<main class="main-content">
    <div class="container">
        <div class="page-header">
            <h2>Registrar Tarea Completada</h2>
            <nav class="breadcrumb">
                <a href="${pageContext.request.contextPath}/home">Dashboard</a> >
                <a href="${pageContext.request.contextPath}/quehaceres">Quehaceres</a> >
                <span>Completar</span>
            </nav>
        </div>

        <jsp:include page="../common/messages.jsp" />

        <section class="form-section">
            <form action="${pageContext.request.contextPath}/quehaceres" method="post" class="complete-form" id="completeTaskForm">
                <input type="hidden" name="action" value="markComplete" />
                
                <div class="form-group">
                    <label for="quehacerId" class="form-label">Seleccionar tarea completada:</label>
                    <select id="quehacerId" name="quehacerId" class="form-select" required onchange="updateTaskDetails()">
                        <option value="">-- Seleccione una tarea --</option>
                        <c:forEach var="quehacer" items="${listaQuehaceres}">
                            <c:if test="${not quehacer.estadoCompletado}">
                                <option value="${quehacer.id}" 
                                        data-miembro="${quehacer.miembroHogar.nombre}"
                                        data-dificultad="${quehacer.dificultad}"
                                        data-limite="${empty quehacer.tiempoLimiteFmt ? quehacer.tiempoLimite : quehacer.tiempoLimiteFmt}"
                                        data-recompensa="${quehacer.recompensa}"
                                        ${param.quehacerId == quehacer.id ? 'selected' : ''}>
                                    ${quehacer.nombre} - Asignado a: ${quehacer.miembroHogar.nombre}
                                    <c:choose>
                                        <c:when test="${quehacer.dificultad == 'FACIL'}"> [🟢 Fácil]</c:when>
                                        <c:when test="${quehacer.dificultad == 'MEDIO'}"> [🟡 Medio]</c:when>
                                        <c:when test="${quehacer.dificultad == 'DIFICIL'}"> [🔴 Difícil]</c:when>
                                    </c:choose>
                                </option>
                            </c:if>
                        </c:forEach>
                    </select>
                    <c:choose>
                        <c:when test="${empty listaQuehaceres or fn:length(listaQuehaceres) == 0}">
                            <small class="form-help error">
                                ⚠️ No hay tareas pendientes. 
                                <a href="${pageContext.request.contextPath}/quehaceres?action=new">Crear nueva tarea</a>
                            </small>
                        </c:when>
                        <c:otherwise>
                            <small class="form-help">Seleccione la tarea que acaba de completar</small>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Información de la tarea seleccionada -->
                <div id="taskDetails" class="task-details-panel" style="display: none;">
                    <h3>📋 Detalles de la tarea</h3>
                    <div class="details-grid">
                        <div class="detail-item">
                            <strong>👤 Miembro asignado:</strong>
                            <span id="taskMember"></span>
                        </div>
                        <div class="detail-item">
                            <strong>📊 Dificultad:</strong>
                            <span id="taskDifficulty"></span>
                        </div>
                        <div class="detail-item">
                            <strong>📅 Fecha límite:</strong>
                            <span id="taskDeadline"></span>
                        </div>
                        <div class="detail-item">
                            <strong>🏆 Recompensa:</strong>
                            <span id="taskReward"></span>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label for="miembroAsignado" class="form-label">Miembro que completó la tarea:</label>
                    <input type="text" 
                           id="miembroAsignado" 
                           name="miembroAsignado" 
                           class="form-input" 
                           readonly 
                           placeholder="Seleccione una tarea para ver el miembro asignado" />
                    <small class="form-help">Este campo se actualiza automáticamente</small>
                </div>

                <div class="form-group">
                    <label for="fechaFinalizacion" class="form-label">Fecha y hora de finalización:</label>
                    <input type="datetime-local" 
                           id="fechaFinalizacion" 
                           name="fechaFinalizacion" 
                           class="form-input" 
                           value="${param.fechaFinalizacion}"
                           required />
                    <small class="form-help">Cuándo se completó realmente la tarea</small>
                </div>

                <div class="form-group">
                    <label for="observaciones" class="form-label">Observaciones (opcional):</label>
                    <textarea id="observaciones" 
                              name="observaciones" 
                              class="form-textarea" 
                              rows="3" 
                              placeholder="Comentarios adicionales sobre la tarea completada..."
                              maxlength="500">${param.observaciones}</textarea>
                    <small class="form-help">Comentarios opcionales sobre cómo se realizó la tarea</small>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-success" id="submitBtn" disabled>
                        ✅ Marcar como Completada
                    </button>
                    <a href="${pageContext.request.contextPath}/quehaceres" class="btn btn-secondary">
                        ↩️ Cancelar
                    </a>
                </div>
            </form>
        </section>

        <!-- Lista de tareas pendientes para referencia -->
        <c:if test="${not empty listaQuehaceres}">
            <aside class="pending-tasks-reference">
                <h3>📋 Tareas pendientes disponibles</h3>
                <div class="tasks-quick-list">
                    <c:set var="pendingCount" value="0" />
                    <c:forEach var="quehacer" items="${listaQuehaceres}">
                        <c:if test="${not quehacer.estadoCompletado}">
                            <c:set var="pendingCount" value="${pendingCount + 1}" />
                            <div class="task-quick-item">
                                <span class="task-name">${quehacer.nombre}</span>
                                <span class="task-assignee">👤 ${quehacer.miembroHogar.nombre}</span>
                                <span class="task-difficulty ${quehacer.dificultad.toString().toLowerCase()}">
                                    <c:choose>
                                        <c:when test="${quehacer.dificultad == 'FACIL'}">🟢</c:when>
                                        <c:when test="${quehacer.dificultad == 'MEDIO'}">🟡</c:when>
                                        <c:when test="${quehacer.dificultad == 'DIFICIL'}">🔴</c:when>
                                    </c:choose>
                                </span>
                            </div>
                        </c:if>
                    </c:forEach>
                    
                    <c:if test="${pendingCount == 0}">
                        <div class="no-pending-tasks">
                            <p>🎉 ¡Felicidades! No hay tareas pendientes.</p>
                            <a href="${pageContext.request.contextPath}/quehaceres?action=new" class="btn btn-primary btn-sm">
                                Crear nueva tarea
                            </a>
                        </div>
                    </c:if>
                </div>
            </aside>
        </c:if>
    </div>
</main>

<!-- JavaScript para funcionalidad dinámica -->
<script>
function updateTaskDetails() {
    const select = document.getElementById('quehacerId');
    const selectedOption = select.options[select.selectedIndex];
    const taskDetails = document.getElementById('taskDetails');
    const memberField = document.getElementById('miembroAsignado');
    const submitBtn = document.getElementById('submitBtn');
    
    if (selectedOption.value) {
        // Mostrar detalles de la tarea
        taskDetails.style.display = 'block';
        document.getElementById('taskMember').textContent = selectedOption.dataset.miembro;
        document.getElementById('taskDifficulty').textContent = selectedOption.dataset.dificultad;
        document.getElementById('taskDeadline').textContent = selectedOption.dataset.limite;
        document.getElementById('taskReward').textContent = selectedOption.dataset.recompensa || 'Por calcular';
        
        // Actualizar campo de miembro
        memberField.value = selectedOption.dataset.miembro;
        
        // Habilitar botón de envío
        submitBtn.disabled = false;
        
        // Establecer fecha actual si no hay una seleccionada
        const fechaInput = document.getElementById('fechaFinalizacion');
        if (!fechaInput.value) {
            const now = new Date();
            const offset = now.getTimezoneOffset() * 60000;
            const localTime = new Date(now.getTime() - offset);
            fechaInput.value = localTime.toISOString().slice(0, 16);
        }
    } else {
        // Ocultar detalles
        taskDetails.style.display = 'none';
        memberField.value = '';
        submitBtn.disabled = true;
    }
}

// Inicializar al cargar la página
document.addEventListener('DOMContentLoaded', function() {
    updateTaskDetails();
});
</script>

<jsp:include page="../common/footer.jsp" />
<jsp:include page="../common/layout-foot.jsp" />