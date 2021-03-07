<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Order overview">
  <h2>Order overview</h2>
  <br>
  <table>
    <thead>
    <tr>
      <td>Image</td>
      <td>
        Description
      </td>
      <td class="quantity">
        Quantity
      </td>
      <td class="price">
        Price
      </td>
    </tr>
    </thead>
    <c:forEach var="item" items="${order.items}" varStatus="status">
      <tr>
        <td>
          <img class="product-tile" src="${item.product.imageUrl}">
        </td>
        <td>
          <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}">
              ${item.product.description}
        </td>
        <td class="quantity">
          <fmt:formatNumber value="${item.quantity}" var="quantity"/>
            ${item.quantity}
        </td>
        <td class="price">
          <c:if test="${fn:length(item.product.priceHistory) > 0}">
            <a href="${pageContext.servletContext.contextPath}/price-history/${item.product.id}"/>
          </c:if>
          <fmt:formatNumber value="${item.product.price}" type="currency"
                            currencySymbol="${item.product.currency.symbol}"/>
        </td>
      </tr>
    </c:forEach>
    <tr>
      <td></td>
      <td></td>
      <td class="quantity">Total quantity:</td>
      <td class="quantity">
          ${order.totalQuantity}
      </td>
    </tr>
    <tr>
      <td></td>
      <td></td>
      <td class="price">Subtotal:</td>
      <td class="price">
        <p>
          <fmt:formatNumber value="${order.subtotal}" type="currency" currencySymbol="${order.currency.symbol}"/>
        </p>
      </td>
    </tr>
    <tr>
      <td></td>
      <td></td>
      <td class="price">Delivery cost:</td>
      <td class="price">
        <p>
          <fmt:formatNumber value="${order.deliveryCost}" type="currency" currencySymbol="${order.currency.symbol}"/>
        </p>
      </td>
    </tr>
    <tr>
      <td></td>
      <td></td>
      <td class="price">Total cost:</td>
      <td class="price">
        <p>
          <fmt:formatNumber value="${order.totalCost}" type="currency" currencySymbol="${order.currency.symbol}"/>
        </p>
      </td>
    </tr>
  </table>
  <h3>Your Details:</h3>
  <table>
    <tags:orderOverviewRow name="firstName" label="First name" order="${order}"></tags:orderOverviewRow>
    <tags:orderOverviewRow name="lastName" label="Last name" order="${order}"></tags:orderOverviewRow>
    <tags:orderOverviewRow name="phone" label="Phone" order="${order}"></tags:orderOverviewRow>
    <tags:orderOverviewRow name="deliveryDate" label="Delivery date" order="${order}"></tags:orderOverviewRow>
    <tags:orderOverviewRow name="deliveryAddress" label="Delivery address" order="${order}"></tags:orderOverviewRow>
    <tr>
      <td>Payment method</td>
      <td>
          ${order.paymentMethod}
      </td>
    </tr>
  </table>
</tags:master>
