<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="Ranking y Ligas" scope="request" />
<c:set var="bodyClass" value="ranking-page" scope="request" />

<jsp:include page="../common/layout-head.jsp" />
<jsp:include page="../common/header.jsp" />

<main class="main-content">
  <div class="container">
    <div class="page-header">
      <h2>🏆 Ranking General y por Ligas</h2>
      <nav class="breadcrumb">
        <a href="${pageContext.request.contextPath}/home">Dashboard</a> >
        <span>Ranking</span>
      </nav>
    </div>

    <!-- Top global -->
    <section class="section-card">
      <h3>🌐 Top Global</h3>
      <div class="table-responsive">
        <table class="data-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Miembro</th>
              <th>Liga</th>
              <th>Puntos</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="m" items="${rankingTopGlobal}" varStatus="st">
              <tr>
                <td>${st.index + 1}</td>
                <td>${m.nombre}</td>
                <td>
                  <span class="role-badge ${m.liga == 'ORO' ? 'oro' : m.liga == 'PLATA' ? 'plata' : 'bronce'}">
                    <c:out value="${m.liga}" />
                  </span>
                </td>
                <td><strong>${m.puntos}</strong></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </section>

    <!-- Por liga -->
    <section class="section-card">
      <h3>🥇🥈🥉 Ranking por Liga</h3>
      <c:forEach var="entry" items="${rankingPorLiga}">
        <h4 style="margin-top:12px;">${entry.key}</h4>
        <div class="table-responsive">
          <table class="data-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Miembro</th>
                <th>Puntos</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="m" items="${entry.value}" varStatus="st">
                <tr>
                  <td>${st.index + 1}</td>
                  <td>${m.nombre}</td>
                  <td>${m.puntos}</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </c:forEach>
    </section>

    <!-- Rachas -->
    <section class="section-card" id="rachas">
      <h3>🔥 Ranking de Rachas</h3>
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
            <c:forEach var="m" items="${rankingMiembrosRacha}" varStatus="st">
              <c:set var="racha" value="${rankingRachaPorMiembro[m.id]}" />
              <c:if test="${empty racha}"><c:set var="racha" value="0" /></c:if>
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
