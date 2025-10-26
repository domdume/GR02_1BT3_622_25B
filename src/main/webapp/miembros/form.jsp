<%-- 
    Formulario de Miembros - Solo formulario de entrada
    Responsabilidad: Capturar datos del usuario y enviar al servlet
    Sin lógica de negocio - Solo vista pura MVC
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="Registrar Miembro" scope="request" />
<c:set var="bodyClass" value="form-page" scope="request" />

<jsp:include page="../common/layout-head.jsp" />
<jsp:include page="../common/header.jsp" />

<main class="main-content">
    <div class="container">
        <div class="page-header">
            <h2>Registrar Nuevo Miembro</h2>
            <nav class="breadcrumb">
                <a href="${pageContext.request.contextPath}/home">Dashboard</a> >
                <a href="${pageContext.request.contextPath}/miembros">Miembros</a> >
                <span>Nuevo</span>
            </nav>
        </div>

        <jsp:include page="../common/messages.jsp" />

        <section class="form-section">
            <form action="${pageContext.request.contextPath}/miembros" method="post" class="member-form">
                <input type="hidden" name="action" value="insert" />
                
                <div class="form-group">
                    <label for="nombre" class="form-label">Nombre completo:</label>
                    <input type="text" 
                           id="nombre" 
                           name="nombre" 
                           class="form-input" 
                           placeholder="Ingrese el nombre del miembro"
                           value="${param.nombre}"
                           required 
                           maxlength="100" />
                    <small class="form-help">Nombre del miembro del hogar</small>
                </div>

                <div class="form-group">
                    <label for="edad" class="form-label">Edad:</label>
                    <input type="number" 
                           id="edad" 
                           name="edad" 
                           class="form-input" 
                           placeholder="Edad en años"
                           value="${param.edad}"
                           min="1" 
                           max="120" 
                           required />
                    <small class="form-help">Edad del miembro (1-120 años)</small>
                </div>

                <div class="form-group">
                    <label for="esJefe" class="form-label">Rol en el hogar:</label>
                    <div class="radio-group">
                        <label class="radio-option">
                            <input type="radio" 
                                   name="esJefe" 
                                   value="true" 
                                   ${param.esJefe == 'true' ? 'checked' : ''} />
                            <span class="radio-label">👑 Jefe del Hogar</span>
                        </label>
                        <label class="radio-option">
                            <input type="radio" 
                                   name="esJefe" 
                                   value="false" 
                                   ${param.esJefe == 'false' || empty param.esJefe ? 'checked' : ''} />
                            <span class="radio-label">👤 Miembro Regular</span>
                        </label>
                    </div>
                    <small class="form-help">
                        <c:choose>
                            <c:when test="${tieneJefe}">
                                ⚠️ Ya existe un jefe del hogar. Los nuevos miembros serán regulares.
                            </c:when>
                            <c:otherwise>
                                💡 El primer miembro puede ser designado como jefe del hogar.
                            </c:otherwise>
                        </c:choose>
                    </small>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">
                        💾 Registrar Miembro
                    </button>
                    <a href="${pageContext.request.contextPath}/miembros" class="btn btn-secondary">
                        ↩️ Cancelar
                    </a>
                </div>
            </form>
        </section>

        <!-- Información del sistema Observer -->
    </div>
</main>

<jsp:include page="../common/footer.jsp" />
<jsp:include page="../common/layout-foot.jsp" />
