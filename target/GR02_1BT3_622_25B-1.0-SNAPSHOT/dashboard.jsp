<%-- 
    Dashboard Principal - Vista unificada que reemplaza tablero.jsp
    Responsabilidad: Solo mostrar datos proporcionados por HomeServlet
    Sin lógica de negocio - Solo vista pura MVC
--%>
<c:set var="pageTitle" value="Dashboard del Hogar" scope="request" />
<c:set var="bodyClass" value="dashboard-page" scope="request" />

<jsp:include page="common/layout-head.jsp" />
<jsp:include page="common/header.jsp" />

<main class="main-content">
    <div class="container">
        <jsp:include page="common/messages.jsp" />
        
        <!-- Panel de acciones rápidas del Jefe del Hogar -->
        <c:if test="${tieneJefe}">
            <section class="quick-actions">
                <h2>Acciones del Jefe del Hogar</h2>
                <div class="action-buttons">
                    <a href="${pageContext.request.contextPath}/miembros?action=new" class="btn btn-primary">
                        👥 Registrar Miembro
                    </a>
                    <a href="${pageContext.request.contextPath}/quehaceres?action=new" class="btn btn-primary">
                        📋 Crear Quehacer
                    </a>
                    <a href="${pageContext.request.contextPath}/incentivos?action=new" class="btn btn-primary">
                        🏆 Gestionar Incentivos
                    </a>
                </div>
            </section>
        </c:if>

        <!-- Panel de acciones para miembros -->
        <section class="member-actions">
            <h2>Acciones de Miembros</h2>
            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/quehaceres?action=pending" class="btn btn-secondary">
                    ⏳ Ver Pendientes
                </a>
                <a href="${pageContext.request.contextPath}/quehaceres?action=complete" class="btn btn-secondary">
                    ✅ Completar Tarea
                </a>
                <a href="${pageContext.request.contextPath}/incentivos" class="btn btn-secondary">
                    📊 Ver Historial
                </a>
            </div>
        </section>

        <!-- Resumen de quehaceres del hogar -->
        <section class="quehaceres-summary">
            <h2>Quehaceres del Hogar</h2>
            
            <c:choose>
                <c:when test="${not empty listaQuehaceres}">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Quehacer</th>
                                    <th>Asignado a</th>
                                    <th>Dificultad</th>
                                    <th>Estado</th>
                                    <th>Fecha Límite</th>
                                    <th>Puntos</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="quehacer" items="${listaQuehaceres}">
                                    <tr class="${quehacer.estadoCompletado ? 'completed' : 'pending'}">
                                        <td><c:out value="${quehacer.nombre}" /></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty quehacer.miembroHogar}">
                                                    <c:out value="${quehacer.miembroHogar.nombre}" />
                                                </c:when>
                                                <c:otherwise>Sin asignar</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <span class="difficulty-badge ${quehacer.dificultad.toString().toLowerCase()}">
                                                <c:out value="${quehacer.dificultad}" />
                                            </span>
                                        </td>
                                        <td>
                                            <span class="status-badge ${quehacer.estadoCompletado ? 'completed' : 'pending'}">
                                                ${quehacer.estadoCompletado ? '✅ Completado' : '⏳ Pendiente'}
                                            </span>
                                        </td>
                                        <td>
                                            <c:if test="${not empty quehacer.tiempoLimite}">
                                                <fmt:formatDate value="${quehacer.tiempoLimite}" pattern="dd/MM/yyyy HH:mm" />
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${not empty quehacer.miembroHogar}">
                                                <span class="points-badge">
                                                    ${quehacer.miembroHogar.puntos} pts
                                                </span>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <p>No hay quehaceres registrados en el hogar.</p>
                        <c:if test="${tieneJefe}">
                            <a href="${pageContext.request.contextPath}/quehaceres?action=new" class="btn btn-primary">
                                Crear primer quehacer
                            </a>
                        </c:if>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <!-- Resumen de miembros del hogar -->
        <section class="members-summary">
            <h2>Miembros del Hogar</h2>
            
            <c:choose>
                <c:when test="${not empty listaMiembros}">
                    <div class="members-grid">
                        <c:forEach var="miembro" items="${listaMiembros}">
                            <div class="member-card">
                                <div class="member-info">
                                    <h3><c:out value="${miembro.nombre}" /></h3>
                                    <p class="member-details">
                                        ${miembro.edad} años | ${miembro.puntos} puntos
                                    </p>
                                    <c:if test="${miembro.getClass().simpleName == 'JefeDelHogar'}">
                                        <span class="role-badge jefe">👑 Jefe del Hogar</span>
                                    </c:if>
                                </div>
                                <div class="member-stats">
                                    <span class="stat">
                                        📋 ${fn:length(miembro.quehaceres)} tareas
                                    </span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <p>No hay miembros registrados en el hogar.</p>
                        <a href="${pageContext.request.contextPath}/miembros?action=new" class="btn btn-primary">
                            Registrar primer miembro
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <!-- Estadísticas del sistema Observer -->
        <c:if test="${not empty observerStats}">
            <section class="observer-stats">
                <h2>Estado del Sistema Observer</h2>
                <div class="stats-grid">
                    <div class="stat-card">
                        <span class="stat-value">${observerStats.observadoresSuscritos}</span>
                        <span class="stat-label">Observadores Suscritos</span>
                    </div>
                    <div class="stat-card">
                        <span class="stat-value">${observerStats.notificacionesEnviadas}</span>
                        <span class="stat-label">Notificaciones Enviadas</span>
                    </div>
                    <div class="stat-card">
                        <span class="stat-value">${observerStats.quehaceresEnCola}</span>
                        <span class="stat-label">Tareas en Cola</span>
                    </div>
                </div>
            </section>
        </c:if>
    </div>
</main>

<jsp:include page="common/footer.jsp" />
<jsp:include page="common/layout-foot.jsp" />