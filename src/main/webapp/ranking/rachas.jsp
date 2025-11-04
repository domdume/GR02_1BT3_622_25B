<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="Ranking de Rachas" scope="request" />
<c:set var="bodyClass" value="ranking-page" scope="request" />

<jsp:include page="../common/layout-head.jsp" />
<jsp:include page="../common/header.jsp" />

<main class="main-content">
  <div class="container">
    <div class="page-header">
      <h2>🔥 Ranking de Rachas</h2>
      <nav class="breadcrumb">
        <a href="${pageContext.request.contextPath}/home">Dashboard</a> >
        <a href="${pageContext.request.contextPath}/ranking">Ranking</a> >
        <span>Rachas</span>
      </nav>
    </div>

    <section class="section-card">
      <h3>Miembros ordenados por racha</h3>
      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Miembro</th>
              <th>Racha (días)</th>
            </tr>
          </thead>
          <tbody>
            <c:set var="prevRacha" value="-1" />
            <c:set var="rank" value="0" />
            <c:forEach var="m" items="${miembros}" varStatus="st">
              <c:set var="racha" value="${rachaPorMiembro[m.id]}" />
              <c:if test="${empty racha}"><c:set var="racha" value="0" /></c:if>
              <!-- Si racha cambia, el rank es el índice+1; si empata, conserva el rank anterior -->
              <c:choose>
                <c:when test="${racha != prevRacha}">
                  <c:set var="rank" value="${st.index + 1}" />
                  <c:set var="prevRacha" value="${racha}" />
                </c:when>
              </c:choose>
              <tr>
                <td>${rank}</td>
                <td>${m.nombre}</td>
                <td><strong>${racha}</strong></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
      <p class="muted">Empates se muestran con la misma posición y se resuelven alfabéticamente por nombre.</p>
    </section>
  </div>
</main>

<jsp:include page="../common/footer.jsp" />
<jsp:include page="../common/layout-foot.jsp" />
