package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCartItemServletTest {
    private static final String PATH_INFO = "/1";
    private static final String CONTEXT_PATH = "";

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletConfig servletConfig;
    @Mock
    private CartService cartService;
    @Mock
    private Cart cart;
    @Mock
    private ProductDao productDao;
    @Mock
    private Product testProduct;

    private DeleteCartItemServlet servlet = new DeleteCartItemServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(servletConfig, cartService, productDao);
    }

    @Test
    public void shouldTestDoPost() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(PATH_INFO);
        when(productDao.getProduct(anyLong())).thenReturn(testProduct);
        when(cartService.getCart(any())).thenReturn(cart);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
    }
}