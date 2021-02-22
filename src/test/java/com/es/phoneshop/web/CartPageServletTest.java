package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartPageServletTest {
    private static final String PRODUCT_ID_PARAMETER_NAME = "productId";
    private static final String PRODUCT_QUANTITY_PARAMETER_NAME = "quantity";

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig servletConfig;
    @Mock
    private ProductDao productDao;
    @Mock
    private CartService cartService;
    @Mock
    private Cart cart;

    private CartPageServlet servlet = new CartPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(servletConfig, cartService, productDao);
    }

    @Test
    public void shouldTestDoGet() throws ServletException, IOException {
        when(cartService.getCart(any())).thenReturn(cart);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void shouldTestDoPostWithNoChangesToCartBeingRequested() throws ServletException, IOException {
        when(request.getParameterValues(PRODUCT_ID_PARAMETER_NAME)).thenReturn(new String[]{});
        when(request.getParameterValues(PRODUCT_QUANTITY_PARAMETER_NAME)).thenReturn(new String[]{});

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
    }
}
