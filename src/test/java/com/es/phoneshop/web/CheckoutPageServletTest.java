package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.NotEnoughStockException;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderDao;
import com.es.phoneshop.model.order.OrderService;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.model.product.ProductDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPageServletTest {
    private static final int PRODUCT_QUANTITY = 1;
    private static final String FIRST_NAME_PARAMETER_NAME = "firstName";
    private static final String FIRST_NAME_PARAMETER = "Test Name";
    private static final String LAST_NAME_PARAMETER_NAME = "lastName";
    private static final String LAST_NAME_PARAMETER = "Test Last Name";
    private static final String PHONE_NUMBER_PARAMETER_NAME = "phone";
    private static final String PHONE_NUMBER_PARAMETER = "+0123456789";
    private static final String DELIVERY_ADDRESS_PARAMETER_NAME = "deliveryAddress";
    private static final String DELIVERY_ADDRESS_PARAMETER = "Test city";
    private static final String DELIVERY_DATE_PARAMETER_NAME = "deliveryDate";
    private static final String DELIVERY_DATE_PARAMETER = "2038-01-20";
    private static final String PAYMENT_METHOD_PARAMETER_NAME = "paymentMethod";
    private static final String PAYMENT_METHOD_PARAMETER = PaymentMethod.CASH.name();
    private static final String CONTEXT_PATH = "/1";
    private static final String SECURE_ID = "1234-abcd-5678-efgh";

    @Mock
    private ServletConfig servletConfig;
    @Mock
    private CartService cartService;
    @Mock
    private OrderService orderService;
    @Mock
    private ProductDao productDao;
    @Mock
    private OrderDao orderDao;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private Cart cart;
    @Mock
    private Order order;

    private CheckoutPageServlet servlet = new CheckoutPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(servletConfig, cartService, orderService, productDao, orderDao);
    }

    @Test
    public void shouldTestDoGet() throws ServletException, IOException {
        when(cartService.getCart(any(HttpServletRequest.class))).thenReturn(cart);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void shouldTestDoPost() throws ServletException, IOException, NotEnoughStockException {
        when(cartService.getCart(any(HttpServletRequest.class))).thenReturn(cart);
        when(orderService.getOrder(any())).thenReturn(order);
        when(request.getParameter(eq(FIRST_NAME_PARAMETER_NAME))).thenReturn(FIRST_NAME_PARAMETER);
        when(request.getParameter(eq(LAST_NAME_PARAMETER_NAME))).thenReturn(LAST_NAME_PARAMETER);
        when(request.getParameter(eq(PHONE_NUMBER_PARAMETER_NAME))).thenReturn(PHONE_NUMBER_PARAMETER);
        when(request.getParameter(eq(DELIVERY_ADDRESS_PARAMETER_NAME))).thenReturn(DELIVERY_ADDRESS_PARAMETER);
        when(request.getParameter(eq(DELIVERY_DATE_PARAMETER_NAME))).thenReturn(DELIVERY_DATE_PARAMETER);
        when(request.getParameter(eq(PAYMENT_METHOD_PARAMETER_NAME))).thenReturn(PAYMENT_METHOD_PARAMETER);
        when(order.getTotalQuantity()).thenReturn(PRODUCT_QUANTITY);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        when(order.getSecureId()).thenReturn(SECURE_ID);

        servlet.doPost(request, response);

        verify(order).setFirstName(eq(FIRST_NAME_PARAMETER));
        verify(order).setLastName(eq(LAST_NAME_PARAMETER));
        verify(order).setPhone(eq(PHONE_NUMBER_PARAMETER));
        verify(order).setDeliveryAddress(eq(DELIVERY_ADDRESS_PARAMETER));
        verify(order).setDeliveryDate(any(LocalDate.class));
        verify(order).setPaymentMethod(any(PaymentMethod.class));
        verify(orderService).placeOrder(order, productDao, orderDao);
        verify(cartService).clear(cart);
        verify(response).sendRedirect(anyString());
    }
}
