<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'Sistema de Gestión del Hogar'}" /></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <c:if test="${not empty extraCSS}">
        <c:forEach var="css" items="${extraCSS}">
            <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/${css}">
        </c:forEach>
    </c:if>
</head>
<body class="${bodyClass != null ? bodyClass : 'default-page'}">