package com.es.phoneshop.model.product;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProductSearchEngine {
    private ProductDao productDao;

    public ProductSearchEngine(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        List<Product> products = productDao.getProducts();
        products = filterProductsByAvailability(products);
        if(query != null && !query.chars().allMatch(Character::isWhitespace)) {
            products = filterProductsByQuery(products, query);
            sortResultsByRelevance(products, query);
        }
        sortResultsByUserDefinedOrder(products, sortField, sortOrder);
        return products;
    }

    private long countEntries(String text, String keywords) {
        if(text == null || keywords == null) {
            return 0;
        }
        return Arrays.asList(keywords.trim().split("\\s+"))
                .stream()
                .filter(word -> text.toLowerCase().contains(word.toLowerCase()))
                .count();
    }

    private List<Product> filterProductsByAvailability(List<Product> products) {
        return products
                .stream()
                .filter(product -> product.getPrice() != null)
                .filter(product -> product.getStock() > 0)
                .collect(Collectors.toList());
    }

    private List<Product> filterProductsByQuery(List<Product> products, String query) {
        return products
                .stream()
                .filter(product -> countEntries(product.getDescription(), query) > 0)
                .collect(Collectors.toList());
    }

    private void sortResultsByRelevance(List<Product> products, String query) {
        products.sort(Comparator.comparing(product -> {
            return (Comparable) ( -countEntries(product.getDescription(), query));
        }));
    }

    private void sortResultsByUserDefinedOrder(List<Product> products, SortField sortField, SortOrder sortOrder) {
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
        products.sort(orderComparator);
    }
}
