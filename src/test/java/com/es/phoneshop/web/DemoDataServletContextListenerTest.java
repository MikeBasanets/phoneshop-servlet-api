package com.es.phoneshop.web;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DemoDataServletContextListenerTest {
    private static int MINIMUM_REQUIRED_PRODUCTS = 1;

    @Mock
    private ProductDao productDao;
    @Mock
    private ServletContextEvent event;

    private DemoDataServletContextListener listener;

    @Before
    public void setup() throws ServletException {
        listener = new DemoDataServletContextListener(productDao);
    }

    @Test
    public void shouldContextInitialised() throws ServletException, IOException {
        listener.contextInitialized(event);

        verify(productDao, atLeast(MINIMUM_REQUIRED_PRODUCTS)).save(any(Product.class));
    }
}
