package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCartServiceTest {
    private static final int PRODUCT_QUANTITY = 1;
    private static final BigDecimal PRODUCT_PRICE = new BigDecimal(1);
    private static final Long PRODUCT_ID = 1L;

    private DefaultCartService defaultCartService;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession httpSession;
    @Mock
    private Cart cart;
    @Mock
    private Product product;
    @Mock
    private CartItem cartItem;

    @Before
    public void setup() {
        defaultCartService = DefaultCartService.getInstance();
    }

    @Test
    public void shouldTestGetCartOfNewSession() {
        when(request.getSession()).thenReturn(httpSession);
        when(httpSession.getAttribute(anyString())).thenReturn(null);

        Cart cart = defaultCartService.getCart(request);

        verify(httpSession).setAttribute(anyString(), any(Cart.class));
        assertNotNull(cart);
    }

    @Test
    public void shouldTestAdd() throws NotEnoughStockException {
        List<CartItem> cartItemList = new ArrayList<>();
        when(cart.getItems()).thenReturn(cartItemList);
        when(product.getStock()).thenReturn(PRODUCT_QUANTITY);
        when(product.getPrice()).thenReturn(PRODUCT_PRICE);

        defaultCartService.add(cart, product, PRODUCT_QUANTITY);

        assertFalse(cartItemList.isEmpty());
    }

    @Test(expected = NotEnoughStockException.class)
    public void shouldTestNotEnoughStock() throws NotEnoughStockException {
        when(cart.getItems()).thenReturn(new ArrayList<>());
        when(product.getStock()).thenReturn(0);

        defaultCartService.add(cart, product, PRODUCT_QUANTITY);
    }

    @Test
    public void shouldTestUpdate() throws NotEnoughStockException {
        List<CartItem> cartItemList = new ArrayList<>();
        when(cart.getItems()).thenReturn(cartItemList);
        when(product.getStock()).thenReturn(PRODUCT_QUANTITY);
        when(product.getPrice()).thenReturn(PRODUCT_PRICE);

        defaultCartService.update(cart, product, PRODUCT_QUANTITY);

        assertFalse(cartItemList.isEmpty());
    }

    @Test
    public void shouldTestDelete() {
        List<CartItem> cartItemList = new ArrayList<>();
        cartItemList.add(cartItem);
        when(cart.getItems()).thenReturn(cartItemList);
        when(cartItem.getProduct()).thenReturn(product);
        when(product.getId()).thenReturn(PRODUCT_ID);

        defaultCartService.delete(cart, product);

        assertTrue(cartItemList.isEmpty());
    }

    @Test
    public void shouldTestClear() {
        List<CartItem> cartItemList = new ArrayList<>();
        cartItemList.add(cartItem);
        when(cart.getItems()).thenReturn(cartItemList);

        defaultCartService.clear(cart);

        assertTrue(cartItemList.isEmpty());
    }
}
