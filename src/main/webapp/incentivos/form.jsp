<%-- 
    Vista de Formulario de Incentivos - Solo formulario de entrada
    Responsabilidad: Capturar datos de incentivos y enviar al servlet
    Sin lógica de negocio - Solo vista pura MVC - Compatible con modelo actual
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="${empty param.id ? 'Crear Incentivo' : 'Editar Incentivo'}" scope="request" />
<c:set var="bodyClass" value="incentivo-form-page" scope="request" />

<jsp:include page="../common/layout-head.jsp" />
<jsp:include page="../common/header.jsp" />

<main class="main-content">
    <div class="container">
        <div class="page-header">
            <h2>
                <c:choose>
                    <c:when test="${empty param.id}">➕ Crear Nuevo Incentivo</c:when>
                    <c:otherwise>✏️ Editar Incentivo</c:otherwise>
                </c:choose>
            </h2>
            <nav class="breadcrumb">
                <a href="${pageContext.request.contextPath}/home">Dashboard</a> >
                <a href="${pageContext.request.contextPath}/incentivos">Incentivos</a> >
                <span>${empty param.id ? 'Crear' : 'Editar'}</span>
            </nav>
        </div>

        <jsp:include page="../common/messages.jsp" />

        <section class="form-section">
            <form action="${pageContext.request.contextPath}/incentivos" method="post" class="incentivo-form" id="incentivoForm">
                <c:choose>
                    <c:when test="${empty param.id}">
                        <input type="hidden" name="action" value="create" />
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="action" value="update" />
                        <input type="hidden" name="id" value="${param.id}" />
                    </c:otherwise>
                </c:choose>

                <div class="form-group">
                    <label for="descripcion" class="form-label">Descripción del incentivo: *</label>
                    <textarea id="descripcion" 
                              name="descripcion" 
                              class="form-textarea" 
                              rows="4" 
                              placeholder="Ej: Dinero extra por completar tareas, Salida al cine, Reconocimiento especial..."
                              required 
                              maxlength="500">${param.descripcion}</textarea>
                    <small class="form-help">Descripción clara del incentivo o recompensa</small>
                </div>

                <div class="form-row">
                    <div class="form-group col-half">
                        <label for="tipoIncentivo" class="form-label">Tipo de incentivo: *</label>
                        <select id="tipoIncentivo" name="tipoIncentivo" class="form-select" required onchange="updateIncentivoHelp()">
                            <option value="">-- Seleccione el tipo --</option>
                            <option value="RECOMPENSA" ${param.tipoIncentivo == 'RECOMPENSA' ? 'selected' : ''}>
                                🏆 Recompensa
                            </option>
                            <option value="PENALIZACION" ${param.tipoIncentivo == 'PENALIZACION' ? 'selected' : ''}>
                                ⚠️ Penalización
                            </option>
                        </select>
                        <small class="form-help" id="incentivoHelp">Seleccione el tipo de incentivo</small>
                    </div>

                    <div class="form-group col-half">
                        <label for="puntos" class="form-label">Puntos:</label>
                        <input type="number" 
                               id="puntos" 
                               name="puntos" 
                               class="form-input" 
                               value="${param.puntos}"
                               min="-100" 
                               max="100"
                               placeholder="0" />
                        <small class="form-help">Puntos a asignar (positivos para recompensas, negativos para penalizaciones)</small>
                    </div>
                </div>

                <!-- Información del sistema -->
                <div class="form-section-info">
                    <h3>ℹ️ Información importante</h3>
                    <div class="info-grid">
                        <div class="info-card">
                            <h4>🏆 Recompensas</h4>
                            <p>Se otorgan automáticamente cuando los miembros completan tareas a tiempo. Los puntos suelen ser positivos.</p>
                        </div>
                        <div class="info-card">
                            <h4>⚠️ Penalizaciones</h4>
                            <p>Se aplican cuando las tareas no se completan en el tiempo establecido. Los puntos suelen ser negativos.</p>
                        </div>
                    </div>
                    <p><strong>Nota:</strong> El sistema Observer gestiona automáticamente la asignación de incentivos basándose en el comportamiento de los miembros.</p>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary" id="submitBtn">
                        <c:choose>
                            <c:when test="${empty param.id}">➕ Crear Incentivo</c:when>
                            <c:otherwise>💾 Actualizar Incentivo</c:otherwise>
                        </c:choose>
                    </button>
                    <a href="${pageContext.request.contextPath}/incentivos" class="btn btn-secondary">
                        ↩️ Cancelar
                    </a>
                </div>
            </form>
        </section>

        <!-- Ejemplos de incentivos -->
        <aside class="examples-panel">
            <h3>💡 Ejemplos de incentivos</h3>
            <div class="examples-grid">
                <div class="example-card recompensa">
                    <h4>🏆 Recompensas (Puntos positivos)</h4>
                    <ul>
                        <li><strong>+20 puntos:</strong> Tarea completada a tiempo</li>
                        <li><strong>+30 puntos:</strong> Tarea difícil completada</li>
                        <li><strong>+10 puntos:</strong> Tarea fácil completada</li>
                        <li><strong>Descripción:</strong> "Completado a tiempo: [nombre tarea]"</li>
                    </ul>
                </div>
                <div class="example-card penalizacion">
                    <h4>⚠️ Penalizaciones (Puntos negativos)</h4>
                    <ul>
                        <li><strong>-5 puntos:</strong> Tarea no completada a tiempo</li>
                        <li><strong>-10 puntos:</strong> Tarea vencida</li>
                        <li><strong>-15 puntos:</strong> Tarea abandonada</li>
                        <li><strong>Descripción:</strong> "No completado a tiempo: [nombre tarea]"</li>
                    </ul>
                </div>
            </div>
        </aside>

        <!-- Información del sistema Observer -->
    </div>
</main>

<!-- JavaScript para funcionalidad dinámica -->
<script>
function updateIncentivoHelp() {
    const select = document.getElementById('tipoIncentivo');
    const helpText = document.getElementById('incentivoHelp');
    const puntosField = document.getElementById('puntos');
    
    const helpMessages = {
        'RECOMPENSA': '🏆 Incentivos positivos por buen comportamiento o tareas completadas',
        'PENALIZACION': '⚠️ Penalizaciones por tareas no completadas o comportamiento inadecuado'
    };
    
    helpText.textContent = helpMessages[select.value] || 'Seleccione el tipo de incentivo';
    
    // Sugerir valor de puntos según el tipo
    if (select.value === 'RECOMPENSA' && !puntosField.value) {
        puntosField.placeholder = '20';
    } else if (select.value === 'PENALIZACION' && !puntosField.value) {
        puntosField.placeholder = '-10';
    }
}

// Validación del formulario
document.getElementById('incentivoForm').addEventListener('submit', function(e) {
    const descripcion = document.getElementById('descripcion').value.trim();
    const tipo = document.getElementById('tipoIncentivo').value;
    
    if (!descripcion) {
        e.preventDefault();
        alert('⚠️ La descripción del incentivo es obligatoria');
        document.getElementById('descripcion').focus();
        return;
    }
    
    if (!tipo) {
        e.preventDefault();
        alert('⚠️ Debe seleccionar un tipo de incentivo');
        document.getElementById('tipoIncentivo').focus();
        return;
    }
});

// Inicializar al cargar la página
document.addEventListener('DOMContentLoaded', function() {
    updateIncentivoHelp();
    console.log('Formulario de incentivos cargado - Compatible con modelo actual');
});
</script>

<jsp:include page="../common/footer.jsp" />
<jsp:include page="../common/layout-foot.jsp" />