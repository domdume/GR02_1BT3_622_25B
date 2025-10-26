<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // Redirigir automáticamente al dashboard principal
    response.sendRedirect(request.getContextPath() + "/home");
%>
