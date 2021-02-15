package com.es.phoneshop.web;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PriceHistoryPageServletTest {
    private static final String PRODUCT_PAGE_RELATIVE_PATH = "/WEB-INF/pages/priceHistory.jsp";
    private static final String PRODUCT_PATH = "/1";

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

    private PriceHistoryPageServlet servlet = new PriceHistoryPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(servletConfig, productDao, recentlyViewedProductsService);
    }

    @Test
    public void shouldTestDoGet() throws ServletException, IOException {
        when(productDao.getProduct(anyLong())).thenReturn(product);
        when(request.getPathInfo()).thenReturn(PRODUCT_PATH);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(recentlyViewedProductsService.getRecentlyViewedProducts(any())).thenReturn(recentlyViewedProducts);
        when(recentlyViewedProducts.getItems()).thenReturn(new ArrayList<>());

        servlet.doGet(request, response);

        verify(request).setAttribute(anyString(), any(Product.class));
        verify(request).getRequestDispatcher(eq(PRODUCT_PAGE_RELATIVE_PATH));
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(anyString(), any(List.class));
    }
}
