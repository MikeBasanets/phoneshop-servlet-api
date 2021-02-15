package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.NotEnoughStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.recentlyviewed.RecentlyViewedProducts;
import com.es.phoneshop.model.recentlyviewed.RecentlyViewedProductsService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
    private static final String REQUEST_ATTRIBUTE = "product";
    private static final String PRODUCT_PAGE_RELATIVE_PATH = "/WEB-INF/pages/product.jsp";
    private static final String PRODUCT_PATH = "/1";
    private static final String QUANTITY_PARAMETER_NAME = "quantity";
    private static final String PRODUCT_QUANTITY = "1,000";
    private static final Locale LOCALE = Locale.US;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig servletConfig;
    @Mock
    private Product product;
    @Mock
    private ProductDao productDao;
    @Mock
    private RecentlyViewedProductsService recentlyViewedProductsService;
    @Mock
    private RecentlyViewedProducts recentlyViewedProducts;
    @Mock
    private CartService cartService;
    @Mock
    private Cart cart;

    private ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(servletConfig, productDao, cartService, recentlyViewedProductsService);
        when(productDao.getProduct(anyLong())).thenReturn(product);
        when(request.getPathInfo()).thenReturn(PRODUCT_PATH);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void shouldTestDoGet() throws ServletException, IOException {
        when(recentlyViewedProductsService.getRecentlyViewedProducts(any())).thenReturn(recentlyViewedProducts);
        when(recentlyViewedProducts.getItems()).thenReturn(new ArrayList<>());

        servlet.doGet(request, response);

        verify(request).setAttribute(anyString(), any(Product.class));
        verify(request).setAttribute(anyString(), any(List.class));
        verify(request).setAttribute(eq(REQUEST_ATTRIBUTE), any());
        verify(request).getRequestDispatcher(eq(PRODUCT_PAGE_RELATIVE_PATH));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void shouldTestDoPost() throws ServletException, IOException, NotEnoughStockException {
        when(request.getParameter(QUANTITY_PARAMETER_NAME)).thenReturn(PRODUCT_QUANTITY);
        when(cartService.getCart(any())).thenReturn(cart);
        when(request.getLocale()).thenReturn(LOCALE);

        servlet.doPost(request, response);

        verify(cartService).add(any(), any(), anyInt());
        verify(response).sendRedirect(anyString());
    }
}
