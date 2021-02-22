<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<tags:master pageTitle="Product Details">
    <c:if test="${not empty param.message}">
        <c:if test="${empty error}">
            <div class="success">
                ${param.message}
            </div>
        </c:if>
    </c:if>
    <c:if test="${not empty error}">
        <div class="error">
            Error occurred while adding product to cart
        </div>
    </c:if>
    <p>
        <br>
        ${product.description}
    </p>
    <form method="post">
        <table>
            <tr>
                <td>Image</td>
                <td>
                    <img src="${product.imageUrl}">
                </td>
            </tr>
            <tr>
                <td>Code</td>
                <td>
                        ${product.code}
                </td>
            </tr>
            <tr>
                <td>Stock</td>
                <td>
                    ${product.stock}
                </td>
            </tr>
            <tr>
                <td>Price</td>
                <td class="price">
                    <c:if test="${fn:length(product.priceHistory) > 0}">
                        <a href="${pageContext.servletContext.contextPath}/price-history/${product.id}">
                    </c:if>
                    <fmt:formatNumber value="${product.price}" type="currency"
                                      currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>
            <tr>
                <td>Quantity</td>
                <td>
                    <input name="quantity" value="${not empty error ? param.quantity : 1}" class="quantity">
                    <c:if test="${not empty error}">
                        <div class="error">
                            ${error}
                        </div>
                    </c:if>
                </td>
            </tr>
        </table>
        <button>Add to cart</button>
    </form>
    <br>
    <tags:recentlyViewed recentlyViewedProducts="${recentlyViewedProducts}"/>
    <br>
    <tags:footer></tags:footer>
</tags:master>