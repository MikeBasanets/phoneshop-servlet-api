<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Checkout">
  <br>
  <c:if test="${not empty errors['notEnoughStock']}">
    <div class="error">
        ${errors['notEnoughStock']}
      <br><br>
    </div>
  </c:if>
  <c:if test="${not empty errors['emptyOrder']}">
    <div class="error">
        ${errors['emptyOrder']}
      <br><br>
    </div>
  </c:if>
  <form method="post">
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
      <tags:orderFormRow name="firstName" label="First name" order="${order}" errors="${errors}"></tags:orderFormRow>
      <tags:orderFormRow name="lastName" label="Last name" order="${order}" errors="${errors}"></tags:orderFormRow>
      <tags:orderFormRow name="phone" label="Phone" order="${order}" errors="${errors}"></tags:orderFormRow>
      <tr>
        <td>Delivery date<span style="color: red">*</span></td>
        <td>
          <c:set var="error" value="${errors['deliveryDate']}"/>
          <input type="date" name="deliveryDate" value="${not empty error ? param['deliveryDate'] : order['deliveryDate']}"/>
          <c:if test="${not empty error}">
            <div class="error">
                ${error}
            </div>
          </c:if>
        </td>
      </tr>
      <tags:orderFormRow name="deliveryAddress" label="Delivery address" order="${order}" errors="${errors}"></tags:orderFormRow>
      <tr>
        <td>Payment method<span style="color: red">*</span></td>
        <td>
          <label>
            <select name="paymentMethod">
              <c:set var="error" value="${errors['paymentMethod']}"/>
              <option>${not empty error ? param['paymentMethod'] : order.paymentMethod}</option>
              <c:forEach var="paymentMethod" items="${paymentMethods}">
                <c:if test="${paymentMethod != order.paymentMethod}">
                  <option>${paymentMethod}</option>
                </c:if>
              </c:forEach>
            </select>
          </label>
          <c:if test="${not empty error}">
            <div class="error">
                ${error}
            </div>
          </c:if>
        </td>
      </tr>
    </table>
    <button>Place order</button>
  </form>
  <br>
  <tags:footer></tags:footer>
</tags:master>
