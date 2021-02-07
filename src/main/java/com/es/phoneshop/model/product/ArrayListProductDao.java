package com.es.phoneshop.model.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Currency;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArrayListProductDao implements ProductDao {
    private static final String NULL_PASSED_EXCEPTION_MESSAGE = "Null passed as parameter instead of Product";
    private static final String INEXISTENT_ID_EXCEPTION_MESSAGE = "Product with such Id not found";

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();
    private long maxId = 0L;
    private List<Product> products = new ArrayList<>();

    public ArrayListProductDao() {
        saveSampleProducts();
    }

    public List<Product> getProducts() {
        readLock.lock();
        List<Product> result = new ArrayList<>(products);
        readLock.unlock();
        return result;
    }

    public void setProducts(List<Product> products) {
        writeLock.lock();
        this.products = new ArrayList<>(products);
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
                    .orElseThrow(() -> new NoSuchProductException(INEXISTENT_ID_EXCEPTION_MESSAGE));
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public List<Product> findProducts(String query) {
        readLock.lock();
        boolean queryIsEntered = (query != null && !query.isBlank());
        Comparator<Product> comparator = Comparator.comparing(product -> {
            return (Comparable) countEntries(product.getDescription(), query);
        });
        List<Product> result = products
                .stream()
                .filter(product -> !queryIsEntered || countEntries(product.getDescription(), query) > 0)
                .filter(product -> product.getPrice() != null)
                .filter(product -> product.getStock() > 0)
                .sorted(comparator.reversed())
                .collect(Collectors.toList());
        readLock.unlock();
        return result;
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

    private void saveSampleProducts() {
        Currency usd = Currency.getInstance("USD");
        save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
        save(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"));
        save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        save(new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"));
        save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        save(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"));
        save(new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"));
        save(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"));
        save(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"));
        save(new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"));
        save(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"));
        save(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
    }

    private long countEntries(String text, String keywords) {
        if(text == null || keywords == null) {
            return 0;
        }
        return Arrays.asList(keywords.split(" "))
                .stream()
                .filter(word -> text.toLowerCase().contains(word.toLowerCase()))
                .count();
    }
}
