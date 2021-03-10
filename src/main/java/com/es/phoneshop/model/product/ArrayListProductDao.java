package com.es.phoneshop.model.product;

import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.cart.NotEnoughStockException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

public class ArrayListProductDao implements ProductDao {
    private static final String NULL_PASSED_EXCEPTION_MESSAGE = "Null passed as parameter instead of Product";
    private static final String NONEXISTENT_ID_EXCEPTION_MESSAGE = "Product with such Id not found";

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();
    private long maxId = 0L;
    private List<Product> products = new ArrayList<>();

    private static class SingletonHolder {
        private static final ArrayListProductDao INSTANCE = new ArrayListProductDao();
    }

    private ArrayListProductDao() {
    }

    public static ArrayListProductDao getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public List<Product> getProducts() {
        readLock.lock();
        List<Product> result = new ArrayList<>(products);
        readLock.unlock();
        return result;
    }

    @Override
    public void setProducts(List<Product> products) {
        writeLock.lock();
        this.products = new ArrayList<>(products);
        maxId = 0;
        products.forEach(product -> maxId = Math.max(maxId, product.getId()));
        writeLock.unlock();
    }

    @Override
    public Product getProduct(Long id) throws NoSuchProductException {
        if(id == null) {
            throw new NoSuchProductException(NULL_PASSED_EXCEPTION_MESSAGE);
        }
        readLock.lock();
        try {
            return products
                    .stream()
                    .filter(product -> id.equals(product.getId()))
                    .findAny()
                    .orElseThrow(() -> new NoSuchProductException(NONEXISTENT_ID_EXCEPTION_MESSAGE));
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public void save(Product product) {
        writeLock.lock();
        if(product.getId() == null) {
            product.setId(++maxId);
            products.add(product);
        }
        else {
            int productIndex = IntStream
                    .range(0, products.size())
                    .filter(i -> product.getId().equals(products.get(i).getId()))
                    .findAny()
                    .orElse(-1);
            if(productIndex == -1) {
                products.add(product);
                maxId = Math.max(product.getId(), maxId);
            }
            else {
                products.set(productIndex, product);
            }
        }
        writeLock.unlock();
    }

    @Override
    public void delete(Long id) {
        writeLock.lock();
        if(id != null) {
            products.removeIf(product -> id.equals(product.getId()));
        }
        writeLock.unlock();
    }

    @Override
    public void trySubtractProducts(List<CartItem> items) throws NotEnoughStockException {
        writeLock.lock();
        try {
            for(CartItem cartItem : items) {
                Product product;
                try {
                    product = getProduct(cartItem.getProduct().getId());
                }
                catch (NoSuchProductException exception) {
                    throw new NotEnoughStockException(cartItem.getProduct(), cartItem.getQuantity(), 0);
                }
                if(product.getStock() < cartItem.getQuantity()) {
                    throw new NotEnoughStockException(cartItem.getProduct(), cartItem.getQuantity(), product.getStock());
                }
            }
            items.forEach(cartItem -> {
                Product product = getProduct(cartItem.getProduct().getId());
                product.setStock(product.getStock() - cartItem.getQuantity());
                save(product);
            });
        }
        finally {
            writeLock.unlock();
        }
    }
}
