package com.es.phoneshop.web;

import com.es.phoneshop.model.product.Product;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
    private static final String REQUEST_ATTRIBUTE = "product";
    private static final String PRODUCT_PAGE_RELATIVE_PATH = "/WEB-INF/pages/product.jsp";
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

    private ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(servletConfig, productDao);
        when(productDao.getProduct(anyLong())).thenReturn(product);
        when(request.getPathInfo()).thenReturn(PRODUCT_PATH);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void shouldTestDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).setAttribute(eq(REQUEST_ATTRIBUTE), any());
        verify(request).getRequestDispatcher(eq(PRODUCT_PAGE_RELATIVE_PATH));
        verify(requestDispatcher).forward(request, response);
    }
}
