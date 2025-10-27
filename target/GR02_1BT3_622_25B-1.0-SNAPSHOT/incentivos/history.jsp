<%@ include file="../common/layout-head.jsp" %>
<%@ include file="../common/messages.jsp" %>

<h2>Historial de Incentivos</h2>

<c:forEach var="incentivo" items="${listaIncentivos}">
    <div>
        ${incentivo.nombre} - Fecha: ${incentivo.fechaRegistro}
    </div>
</c:forEach>

<%@ include file="../common/layout-foot.jsp" %>
