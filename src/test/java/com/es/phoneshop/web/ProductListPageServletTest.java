package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ProductSearchEngine;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {
    private static final String PRODUCT_LIST_RELATIVE_PATH = "/WEB-INF/pages/productList.jsp";

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig servletConfig;
    @Mock
    private RecentlyViewedProductsService recentlyViewedProductsService;
    @Mock
    private RecentlyViewedProducts recentlyViewedProducts;
    @Mock
    private ProductSearchEngine productSearchEngine;

    private ProductListPageServlet servlet = new ProductListPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(servletConfig, productSearchEngine, recentlyViewedProductsService);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void shouldTestDoGet() throws ServletException, IOException {
        when(request.getParameter(anyString())).thenReturn(null);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(recentlyViewedProductsService.getRecentlyViewedProducts(any())).thenReturn(recentlyViewedProducts);
        when(recentlyViewedProducts.getItems()).thenReturn(new ArrayList<>());

        servlet.doGet(request, response);

        verify(request, times(2)).setAttribute(anyString(), any(List.class));
        verify(request).getRequestDispatcher(eq(PRODUCT_LIST_RELATIVE_PATH));
        verify(requestDispatcher).forward(request, response);
    }
}
