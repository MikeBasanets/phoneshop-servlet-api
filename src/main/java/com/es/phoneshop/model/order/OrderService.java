package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.NotEnoughStockException;
import com.es.phoneshop.model.product.ProductDao;

import java.util.List;

public interface OrderService {
    Order getOrder(Cart cart);
    List<PaymentMethod> getPaymentMethods();
    void placeOrder(Order order, ProductDao productDao, OrderDao orderDao) throws NotEnoughStockException;
}
