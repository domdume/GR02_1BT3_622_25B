<%@ include file="../common/layout-head.jsp" %>
<%@ include file="../common/messages.jsp" %>

<h2>Incentivos por Miembro</h2>

<c:forEach var="incentivo" items="${listaIncentivos}">
    <div>
        <strong>${incentivo.nombre}</strong> - Miembro: ${incentivo.miembro.nombre}
    </div>
</c:forEach>

<%@ include file="../common/layout-foot.jsp" %>
