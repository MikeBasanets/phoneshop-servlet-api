package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ProductDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContextEvent;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DemoDataServletContextListenerTest {
    @Mock
    private ProductDao productDao;
    @Mock
    private ServletContextEvent event;

    private DemoDataServletContextListener listener;

    @Before
    public void setup() {
        listener = new DemoDataServletContextListener(productDao);
    }

    @Test
    public void shouldContextInitialised() {
        listener.contextInitialized(event);

        verify(productDao).setProducts(any(List.class));
    }
}
