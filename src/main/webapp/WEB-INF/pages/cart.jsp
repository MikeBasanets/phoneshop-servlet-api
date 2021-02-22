<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<tags:master pageTitle="Cart">
    <c:if test="${not empty param.message}">
        <c:if test="${empty errors}">
            <div class="success">
                    ${param.message}
            </div>
        </c:if>
    </c:if>
    <c:if test="${not empty errors}">
        <div class="error">
            Error occurred while updating cart
        </div>
    </c:if>
    <br>
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
                <td>
                </td>
            </tr>
            </thead>
            <c:forEach var="item" items="${cart.items}" varStatus="status">
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
                        <c:set var="error" value="${errors[item.product.id]}"/>
                        <input name="quantity" value="${not empty error ? paramValues['quantity'][status.index] : item.quantity}" class="quantity"/>
                        <c:if test="${not empty error}">
                            <div class="error">
                                    ${errors[item.product.id]}
                            </div>
                        </c:if>
                        <input name="productId" type="hidden" value="${item.product.id}"/>
                    </td>
                    <td class="price">
                        <c:if test="${fn:length(item.product.priceHistory) > 0}">
                        <a href="${pageContext.servletContext.contextPath}/price-history/${item.product.id}">
                            </c:if>
                                <fmt:formatNumber value="${item.product.price}" type="currency"
                                                  currencySymbol="${item.product.currency.symbol}"/>
                    </td>
                    <td>
                        <button form="deleteCartItem"formaction="${pageContext.servletContext.contextPath}/cart/deleteCartItem/${item.product.id}">
                            Delete
                        </button>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <button>Update</button>
    </form>
    <br>
    <form id="deleteCartItem" method="post">
    </form>
    <tags:footer></tags:footer>
</tags:master>