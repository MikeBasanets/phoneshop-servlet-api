package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.NoSuchProductException;
import com.es.phoneshop.model.product.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ArrayListOrderDao implements OrderDao {
    private static final String NONEXISTENT_ID_EXCEPTION_MESSAGE = "Product with such id not found";
    private static final String NONEXISTENT_SECURE_ID_EXCEPTION_MESSAGE = "Product with such secure id not found";

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();
    private List<Order> orders = new ArrayList<>();
    private long maxId = 0L;

    private static class SingletonHolder {
        private static final ArrayListOrderDao INSTANCE = new ArrayListOrderDao();
    }

    private ArrayListOrderDao() {
    }

    public static ArrayListOrderDao getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public List<Order> getOrders() {
        readLock.lock();
        List<Order> result = new ArrayList<>(orders);
        readLock.unlock();
        return result;
    }

    public void setOrders(List<Order> orders) {
        writeLock.lock();
        this.orders = new ArrayList<>(orders);
        maxId = 0L;
        writeLock.unlock();
    }

    @Override
    public Order getOrder(Long id) throws NoSuchOrderException {
        readLock.lock();
        try {
            return orders
                    .stream()
                    .filter(order -> id.equals(order.getId()))
                    .findAny()
                    .orElseThrow(() -> new NoSuchProductException(NONEXISTENT_ID_EXCEPTION_MESSAGE));
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public Order getOrderBySecureId(String secureId) throws NoSuchOrderException {
        readLock.lock();
        try {
            return orders
                    .stream()
                    .filter(order -> secureId.equals(order.getSecureId()))
                    .findAny()
                    .orElseThrow(() -> new NoSuchProductException(NONEXISTENT_SECURE_ID_EXCEPTION_MESSAGE));
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public void save(Order order) {
        writeLock.lock();
        order.setId(++maxId);
        order.setSecureId(generateUniqueSecureId());
        orders.add(order);
        writeLock.unlock();
    }

    private String generateUniqueSecureId() {
        while(true) {
            String secureId = UUID.randomUUID().toString();
            Optional<Order> orderWithDuplicateSecureId = orders
                    .stream()
                    .filter(order -> secureId.equals(order.getSecureId()))
                    .findAny();
            if(!orderWithDuplicateSecureId.isPresent()) {
                return secureId;
            }
        }
    }
}