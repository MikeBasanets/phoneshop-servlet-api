package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.cart.NotEnoughStockException;
import com.es.phoneshop.model.order.ArrayListOrderDao;
import com.es.phoneshop.model.order.DefaultOrderService;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderDao;
import com.es.phoneshop.model.order.OrderService;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CheckoutPageServlet extends HttpServlet {
    private static final String ORDER_ATTRIBUTE_NAME = "order";
    private static final String PAGE_PATH = "/WEB-INF/pages/checkout.jsp";
    private static final String ORDER_OVERVIEW_PAGES = "/order/overview/";
    private static final String FIRST_NAME_PARAMETER_NAME = "firstName";
    private static final String LAST_NAME_PARAMETER_NAME = "lastName";
    private static final String PHONE_NUMBER_PARAMETER_NAME = "phone";
    private static final String DELIVERY_ADDRESS_PARAMETER_NAME = "deliveryAddress";
    private static final String DELIVERY_DATE_PARAMETER_NAME = "deliveryDate";
    private static final String PAYMENT_METHOD_PARAMETER_NAME = "paymentMethod";
    private static final String PAYMENT_METHODS_LIST_ATTRIBUTE = "paymentMethods";
    private static final String ERRORS_ATTRIBUTE = "errors";
    private static final String ORDER_ATTRIBUTE = "order";
    private static final String NO_VALUE_ENTERED_ERROR_MESSAGE = "Please enter value";
    private static final String WRONG_DATE_FORMAT_ERROR_MESSAGE = "Please enter date in yyyy-mm-dd format";
    private static final String NON_FUTURE_DATE_ERROR_MESSAGE = "Please enter future date";
    private static final String NOT_ENOUGH_STOCK_ERROR_KEY = "notEnoughStock";
    private static final String EMPTY_ORDER_ERROR_KEY = "emptyOrder";
    private static final String EMPTY_ORDER_ERROR_MESSAGE = "Your order is empty, please add something to cart before placing it";


    private CartService cartService;
    private OrderService orderService;
    private ProductDao productDao;
    private OrderDao orderDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
        orderService = DefaultOrderService.getInstance();
        productDao = ArrayListProductDao.getInstance();
        orderDao = ArrayListOrderDao.getInstance();
    }

    public void init(ServletConfig config,
                     CartService cartService,
                     OrderService orderService,
                     ProductDao productDao,
                     OrderDao orderDao) throws ServletException {
        super.init(config);
        this.cartService = cartService;
        this.orderService = orderService;
        this.productDao = productDao;
        this.orderDao = orderDao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        request.setAttribute(ORDER_ATTRIBUTE_NAME, orderService.getOrder(cart));
        request.setAttribute(PAYMENT_METHODS_LIST_ATTRIBUTE, orderService.getPaymentMethods());
        request.getRequestDispatcher(PAGE_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        Order order = orderService.getOrder(cart);
        Map<String, String> errors = new HashMap<>();
        setParameter(request, FIRST_NAME_PARAMETER_NAME, errors, order::setFirstName);
        setParameter(request, LAST_NAME_PARAMETER_NAME, errors, order::setLastName);
        setParameter(request, PHONE_NUMBER_PARAMETER_NAME, errors, order::setPhone);
        setParameter(request, DELIVERY_ADDRESS_PARAMETER_NAME, errors, order::setDeliveryAddress);
        setDateParameter(request, errors, order);
        setPaymentMethod(request, errors, order);
        verifyOrderNotEmpty(errors, order);
        if(errors.isEmpty()) {
            tryPlaceOrder(errors, order, cart);
        }
        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + ORDER_OVERVIEW_PAGES + order.getSecureId());
        }
        else {
            request.setAttribute(ERRORS_ATTRIBUTE, errors);
            request.setAttribute(ORDER_ATTRIBUTE, order);
            request.setAttribute(PAYMENT_METHODS_LIST_ATTRIBUTE, orderService.getPaymentMethods());
            request.getRequestDispatcher(PAGE_PATH).forward(request, response);
        }

    }

    private void setParameter(HttpServletRequest request, String parameter, Map<String, String> errors, Consumer<String> consumer) {
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            errors.put(parameter, NO_VALUE_ENTERED_ERROR_MESSAGE);
        }
        else {
            consumer.accept(value);
        }
    }

    private void setDateParameter(HttpServletRequest request, Map<String, String> errors, Order order) {
        String value = request.getParameter(DELIVERY_DATE_PARAMETER_NAME);
        if (value == null || value.isEmpty()) {
            errors.put(DELIVERY_DATE_PARAMETER_NAME, NO_VALUE_ENTERED_ERROR_MESSAGE);
            return;
        }
        try {
            LocalDate dateValue = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
            order.setDeliveryDate(dateValue);
            if(dateValue.isBefore(LocalDate.now())) {
                errors.put(DELIVERY_DATE_PARAMETER_NAME, NON_FUTURE_DATE_ERROR_MESSAGE);
            }
        }
        catch (DateTimeParseException e) {
            errors.put(DELIVERY_DATE_PARAMETER_NAME, WRONG_DATE_FORMAT_ERROR_MESSAGE);
        }
    }

    private void setPaymentMethod(HttpServletRequest request, Map<String, String> errors, Order order) {
        String value = request.getParameter(PAYMENT_METHOD_PARAMETER_NAME);
        if (value == null || value.isEmpty()) {
            errors.put(PAYMENT_METHOD_PARAMETER_NAME, NO_VALUE_ENTERED_ERROR_MESSAGE);
        }
        else {
            order.setPaymentMethod(PaymentMethod.valueOf(value));
        }
    }

    private void verifyOrderNotEmpty(Map<String, String> errors, Order order) {
        if(order.getTotalQuantity() == 0) {
            errors.put(EMPTY_ORDER_ERROR_KEY, EMPTY_ORDER_ERROR_MESSAGE);
        }
    }

    private void tryPlaceOrder(Map<String, String> errors, Order order, Cart cart) {
        try {
            orderService.placeOrder(order, productDao, orderDao);
            cartService.clear(cart);
        }
        catch (NotEnoughStockException exception) {
            errors.put(
                    NOT_ENOUGH_STOCK_ERROR_KEY,
                    "Not enough stock: " +
                            exception.getStockRequested() +
                            " items of " +
                            exception.getProduct().getDescription() +
                            " requested, " +
                            exception.getStockAvailable() +
                            " available"
            );
        }
    }
}
