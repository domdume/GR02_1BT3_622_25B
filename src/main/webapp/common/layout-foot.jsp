<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

    <c:if test="${not empty extraJS}">
        <c:forEach var="js" items="${extraJS}">
            <script src="${pageContext.request.contextPath}/js/${js}"></script>
        </c:forEach>
    </c:if>
</body>
</html>
