<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<tags:master pageTitle="Product Price History">
    <h1>Price History Of Product</h1>
    <h3>${product.description}</h3>
    <c:choose>
        <c:when test="${fn:length(product.priceHistory) == 0}">
            <h5>Sorry, price history for selected product is not available</h5>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <td> Date</td>
                    <td> Price</td>
                </tr>
                </thead>
                <c:forEach var="entry" items="${product.priceHistory}">
                    <tr>
                        <td> ${entry.key} </td>
                        <td>
                            <fmt:formatNumber type="currency" value="${entry.value}"
                                              currencySymbol="${product.currency.symbol}"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>
    <tags:footer></tags:footer>
</tags:master>
