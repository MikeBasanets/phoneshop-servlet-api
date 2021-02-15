<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="recentlyViewedProducts" required="true" type="java.util.List" %>

<c:if test="${not empty recentlyViewedProducts}">
    <h4>Recently Viewed</h4>
    <table>
        <thead>
            <c:forEach var="product" items="${recentlyViewedProducts}">
                <th>
                    <td align="center">
                        <img class="product-tile" src="${product.imageUrl}">
                        <br>
                        <a href=${pageContext.servletContext.contextPath}/products/${product.id}>
                                ${product.description}
                        </a>
                        <br>
                        <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
                    </td>
                </th>
            </c:forEach>
        </thead>
    </table>
</c:if>