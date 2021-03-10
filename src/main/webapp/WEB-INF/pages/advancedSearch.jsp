<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Advanced Search">
  <p>
    Advanced Search
  </p>
  <form>
    <table>
      <tr>
        <td>Query:</td>
        <td><input name="query" value="${param.query}"></td>
      </tr>
      <tr>
        <td>How to match words from query:</td>
        <td>
          <label>
            <select name="queryType">
              <option>${param.queryType}</option>
              <c:forEach var="type" items="${queryTypes}">
                <c:if test="${type != param.queryType}">
                  <option>${type}</option>
                </c:if>
              </c:forEach>
            </select>
          </label>
        </td>
      </tr>
      <tr>
        <td>Min price:</td>
        <td><input name="minPrice" value="${param.minPrice}"></td>
      </tr>
      <tr>
        <td>Max price:</td>
        <td><input name="maxPrice" value="${param.maxPrice}"></td>
      </tr>
    </table>
    <button>Search</button>
  </form>
  <table>
    <thead>
    <tr>
      <td>Image</td>
      <td>Description</td>
      <td>Price</td>
    </tr>
    </thead>
    <c:forEach var="product" items="${products}">
      <tr>
        <td>
          <img class="product-tile" src="${product.imageUrl}">
        </td>
        <td>
          <a href="${pageContext.servletContext.contextPath}/products/${product.id}"/>
            ${product.description}
        </td>
        <td class="price">
          <c:if test="${fn:length(product.priceHistory) > 0}">
            <a href="${pageContext.servletContext.contextPath}/price-history/${product.id}"/>
          </c:if>
          <fmt:formatNumber value="${product.price}" type="currency"
                            currencySymbol="${product.currency.symbol}"/>
        </td>
      </tr>
    </c:forEach>
  </table>
  <tags:footer></tags:footer>
</tags:master>
