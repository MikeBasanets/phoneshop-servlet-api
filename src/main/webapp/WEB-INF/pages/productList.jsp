<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
  <p>
    Welcome to Expert-Soft training!
  </p>
  <c:if test="${not empty param.message}">
    <div class="success">
        ${param.message}
    </div>
  </c:if>
  <form>
    <input name="query" value="${param.query}">
    <input name="sort" type="hidden" value="${param.sort}">
    <input name="order" type="hidden" value="${param.order}">
    <button>Search</button>
  </form>
  <h3>
    <a href="${pageContext.servletContext.contextPath}/advancedSearch"> Go to advanced search </a>
  </h3>
  <table>
    <thead>
    <tr>
      <td>Image</td>
      <td>
        Description
        <tags:sortLink sort="description" order="asc"></tags:sortLink>
        <tags:sortLink sort="description" order="desc"></tags:sortLink>
      </td>
      <td></td>
      <td class="price">
        Price
        <tags:sortLink sort="price" order="asc"></tags:sortLink>
        <tags:sortLink sort="price" order="desc"></tags:sortLink>
      </td>
      <td></td>
    </tr>
    </thead>
    <c:forEach var="product" items="${products}">
      <form method="post">
        <tr>
          <td>
            <img class="product-tile" src="${product.imageUrl}">
          </td>
          <td>
            <a href="${pageContext.servletContext.contextPath}/products/${product.id}"/>
              ${product.description}
          </td>
          <td class="quantity">
            <c:set var="error" value="${errors[product.id]}"/>
            <input name="quantity" value="${not empty error ? param.quantity : 1}" class="quantity"/>
            <c:if test="${not empty error}">
              <div class="error">
                  ${error}
              </div>
            </c:if>
            <input name="productId" type="hidden" value="${product.id}"/>
          </td>
          <td class="price">
            <c:if test="${fn:length(product.priceHistory) > 0}">
              <a href="${pageContext.servletContext.contextPath}/price-history/${product.id}"/>
            </c:if>
            <fmt:formatNumber value="${product.price}" type="currency"
                              currencySymbol="${product.currency.symbol}"/>
          </td>
          <td>
            <button>
              Add to cart
            </button>
          </td>
        </tr>
      </form>
    </c:forEach>
  </table>
  <br>
  <tags:footer></tags:footer>
</tags:master>