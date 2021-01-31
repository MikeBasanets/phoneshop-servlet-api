package com.es.phoneshop.web;

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

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {
    private static final String REQUEST_ATTRIBUTE = "products";
    private static final String PRODUCT_LIST_RELATIVE_PATH = "/WEB-INF/pages/productList.jsp";

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig servletConfig;

    private ProductListPageServlet servlet = new ProductListPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(servletConfig);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void shouldTestDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).setAttribute(eq(REQUEST_ATTRIBUTE), anyCollection());
        verify(request).getRequestDispatcher(eq(PRODUCT_LIST_RELATIVE_PATH));
        verify(requestDispatcher).forward(request, response);
    }
}
