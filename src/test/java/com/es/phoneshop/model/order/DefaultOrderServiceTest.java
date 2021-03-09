package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.cart.NotEnoughStockException;
import com.es.phoneshop.model.product.ProductDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderServiceTest {
    private static final Currency TEST_CURRENCY = Currency.getInstance(Locale.US);
    private static final List<CartItem> CART_ITEM_LIST = new ArrayList<>();

    private DefaultOrderService defaultOrderService;

    @Mock
    private Order order;
    @Mock
    private ProductDao productDao;
    @Mock
    private OrderDao orderDao;

    @Before
    public void setup() {
        defaultOrderService = DefaultOrderService.getInstance();
    }

    @Test
    public void shouldTestGetOrder() {
        Cart cart = new Cart(TEST_CURRENCY);

        Order order = defaultOrderService.getOrder(cart);

        assertNotNull(order);
    }

    @Test
    public void shouldTestPlaceOrder() throws NotEnoughStockException {
        when(order.getItems()).thenReturn(CART_ITEM_LIST);

        defaultOrderService.placeOrder(order, productDao, orderDao);

        verify(productDao).trySubtractProducts(CART_ITEM_LIST);
        verify(orderDao).save(order);

    }
}
