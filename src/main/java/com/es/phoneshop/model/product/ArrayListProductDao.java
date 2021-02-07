package com.es.phoneshop.model.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArrayListProductDao implements ProductDao {
    private static final String NULL_PASSED_EXCEPTION_MESSAGE = "Null passed as parameter instead of Product";
    private static final String INEXISTENT_ID_EXCEPTION_MESSAGE = "Product with such Id not found";

    private static ArrayListProductDao instance;

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();
    private long maxId = 0L;
    private List<Product> products = new ArrayList<>();

    private ArrayListProductDao() {
    }

    public static ArrayListProductDao getInstance() {
        if(instance == null) {
            instance = new ArrayListProductDao();
        }
        return instance;
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
                    .orElseThrow(() -> new NoSuchProductException(INEXISTENT_ID_EXCEPTION_MESSAGE));
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        readLock.lock();
        boolean queryIsEntered = (query != null && !query.chars().allMatch(Character::isWhitespace));
        Comparator<Product> queryRelevanceComparator = Comparator.comparing(product -> {
            return (Comparable) ( -countEntries(product.getDescription(), query));
        });
        Comparator<Product> orderComparator = Comparator.comparing(product -> {
            if(sortField == SortField.description)
                return (Comparable) product.getDescription();
            if(sortField == SortField.price)
                return (Comparable) product.getPrice();
            return (Comparable) 0;
        });
        if(sortOrder == sortOrder.desc) {
            orderComparator = orderComparator.reversed();
        }
        List<Product> result = products
                .stream()
                .filter(product -> !queryIsEntered || countEntries(product.getDescription(), query) > 0)
                .filter(product -> product.getPrice() != null)
                .filter(product -> product.getStock() > 0)
                .sorted(queryRelevanceComparator)
                .sorted(orderComparator)
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
