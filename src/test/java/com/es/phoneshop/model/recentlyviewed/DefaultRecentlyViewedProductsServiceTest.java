package com.es.phoneshop.model.recentlyviewed;

import com.es.phoneshop.model.product.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRecentlyViewedProductsServiceTest {
    private DefaultRecentlyViewedProductsService service;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    RecentlyViewedProducts recentlyViewedProducts;
    @Mock
    Product product;
    
    @Before
    public void setup() {
        service = DefaultRecentlyViewedProductsService.getInstance();
    }

    @Test
    public void shouldTestGetRecentlyViewedProducts() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(anyString())).thenReturn(null);

        RecentlyViewedProducts products = service.getRecentlyViewedProducts(request);

        assertNotNull(products);
    }

    @Test
    public void shouldTestAddProduct() {
        List<Product> recentProductsList = new ArrayList<>();
        when(recentlyViewedProducts.getItems()).thenReturn(recentProductsList);

        service.addProduct(product, recentlyViewedProducts);

        assertFalse(recentProductsList.isEmpty());
    }
}
