<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // Redirigir automáticamente al servlet para cargar los datos dinámicos
    response.sendRedirect(request.getContextPath() + "/quehaceres");
%>