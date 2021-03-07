package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.cart.NotEnoughStockException;
import com.es.phoneshop.model.product.ProductDao;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class DefaultOrderService implements OrderService {
    private static final BigDecimal DEFAULT_DELIVERY_COST = new BigDecimal(5);
    private static final Currency DEFAULT_CURRENCY = Currency.getInstance(Locale.US);

    private static DefaultOrderService instance;

    private DefaultOrderService() {
    }

    public static DefaultOrderService getInstance() {
        if(instance == null) {
            instance = new DefaultOrderService();
        }
        return instance;
    }

    @Override
    public Order getOrder(Cart cart) {
        Order order = new Order(DEFAULT_CURRENCY);
        order.setItems(cart.getItems().stream().map(item -> {
            try {
                return (CartItem) item.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        order.setTotalQuantity(cart.getTotalQuantity());
        order.setSubtotal(cart.getTotalCost());
        order.setDeliveryCost(calculateDeliveryCost());
        order.setTotalCost(order.getSubtotal().add(order.getDeliveryCost()));
        return order;
    }

    private BigDecimal calculateDeliveryCost() {
        return DEFAULT_DELIVERY_COST;
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {
        return Arrays.asList(PaymentMethod.values());
    }

    @Override
    public void placeOrder(Order order, ProductDao productDao, OrderDao orderDao) throws NotEnoughStockException {
        productDao.trySubtractProducts(order.getItems());
        orderDao.save(order);
    }
}
