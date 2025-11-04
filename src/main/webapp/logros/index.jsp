<%--
    Página de listado de Logros
    Muestra por cada miembro si tiene Medalla, Logro de Racha o Emblema.
    Si no tiene ninguno, se muestra el mensaje: "Trabaja duro para tener uno".
    Diseño y includes alineados con el resto de pantallas (miembros, incentivos,...)
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="Listado de Logros" scope="request" />
<c:set var="bodyClass" value="achievements-page" scope="request" />

<jsp:include page="../common/layout-head.jsp" />
<jsp:include page="../common/header.jsp" />

<main class="main-content">
    <div class="container">
        <div class="page-header">
            <h2>Logros del Hogar</h2>
            <div class="header-actions">
                <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">🏠 Dashboard</a>
            </div>
        </div>

        <jsp:include page="../common/messages.jsp" />

        <section class="achievements-list">
            <h3>Miembros y sus logros</h3>

            <c:choose>
                <c:when test="${not empty listaMiembros}">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Miembro</th>
                                    <th class="center">Medalla</th>
                                    <th class="center">Logro Racha</th>
                                    <th class="center">Emblema</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="miembro" items="${listaMiembros}">
                                    <tr>
                                        <td>
                                            <strong><c:out value="${miembro.nombre}"/></strong>
                                            <small>ID: ${miembro.id}</small>
                                        </td>

                                        <%-- preparar nombres para cada tipo de logro (si existen) --%>
                                        <c:set var="medallaNombre" value="" />
                                        <c:set var="rachaNombre" value="" />
                                        <c:set var="emblemaNombre" value="" />
                                        <c:forEach var="l" items="${miembro.logros}">
                                            <c:choose>
                                                <c:when test="${l.tipoLogro != null and l.tipoLogro.name() == 'MEDALLA'}">
                                                    <c:set var="medallaNombre" value="${l.nombre}" />
                                                </c:when>
                                                <c:when test="${l.tipoLogro != null and l.tipoLogro.name() == 'LOGRO_RACHA'}">
                                                    <c:set var="rachaNombre" value="${l.nombre}" />
                                                </c:when>
                                                <c:when test="${l.tipoLogro != null and l.tipoLogro.name() == 'EMBLEMA'}">
                                                    <c:set var="emblemaNombre" value="${l.nombre}" />
                                                </c:when>
                                            </c:choose>
                                        </c:forEach>

                                        <%-- Mostrar cada columna: si tiene nombre mostrarlo (con icono), si no, mostrar el mensaje motivacional --%>
                                        <td class="center">
                                            <c:if test="${not empty medallaNombre}">
                                                <span class="badge achievement medalla">🏅 ${medallaNombre}</span>
                                            </c:if>
                                            <c:if test="${empty medallaNombre}">
                                                <span class="muted">Debes trabajar duro para conseguir un logro</span>
                                            </c:if>
                                        </td>

                                        <td class="center">
                                            <c:if test="${not empty rachaNombre}">
                                                <span class="badge achievement racha">🔥 ${rachaNombre}</span>
                                            </c:if>
                                            <c:if test="${empty rachaNombre}">
                                                <span class="muted">Debes trabajar duro para conseguir un logro</span>
                                            </c:if>
                                        </td>

                                        <td class="center">
                                            <c:if test="${not empty emblemaNombre}">
                                                <span class="badge achievement emblema">🎖️ ${emblemaNombre}</span>
                                            </c:if>
                                            <c:if test="${empty emblemaNombre}">
                                                <span class="muted">Debes trabajar duro para conseguir un logro</span>
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
                        <div class="empty-icon">🏅</div>
                        <h3>No hay miembros registrados</h3>
                        <p>Registra miembros para que puedan obtener logros.</p>
                        <a href="${pageContext.request.contextPath}/miembros?action=new" class="btn btn-primary">Registrar Miembro</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</main>

<jsp:include page="../common/footer.jsp" />
<jsp:include page="../common/layout-foot.jsp" />
