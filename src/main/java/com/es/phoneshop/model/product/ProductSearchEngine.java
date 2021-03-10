package com.es.phoneshop.model.product;

import java.math.BigDecimal;
import java.util.ArrayList;
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
            products = filterProductsByQueryWithAnyMatch(products, query);
            sortResultsByRelevance(products, query);
        }
        sortResultsByUserDefinedOrder(products, sortField, sortOrder);
        return products;
    }

    public List<Product> findProducts(String query, SearchQueryType queryType, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products = productDao.getProducts();
        if(query != null && !query.chars().allMatch(Character::isWhitespace)) {
            if(queryType == SearchQueryType.MATCH_ANY_WORDS) {
                products = filterProductsByQueryWithAnyMatch(products, query);
                sortResultsByRelevance(products, query);
            }
            if(queryType == SearchQueryType.MATCH_ALL_WORDS) {
                products = filterProductsByQueryWithAllMatchingWords(products, query);
            }
        }
        products = filterProductsByPrice(products, minPrice, maxPrice);
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

    private boolean allEntriesFound(String text, String keywords) {
        if(text == null || keywords == null) {
            return false;
        }
        return Arrays.asList(text.trim().toLowerCase().split("\\s+"))
                .containsAll(Arrays.asList(keywords.trim().toLowerCase().split("\\s+")));
    }

    private List<Product> filterProductsByAvailability(List<Product> products) {
        return products
                .stream()
                .filter(product -> product.getPrice() != null)
                .filter(product -> product.getStock() > 0)
                .collect(Collectors.toList());
    }

    private List<Product> filterProductsByPrice(List<Product> products, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> result = new ArrayList<>(products);
        result.removeIf(product -> (minPrice != null && product.getPrice().compareTo(minPrice) < 0));
        result.removeIf(product -> (maxPrice != null && product.getPrice().compareTo(maxPrice) > 0));
        return result;
    }

    private List<Product> filterProductsByQueryWithAnyMatch(List<Product> products, String query) {
        return products
                .stream()
                .filter(product -> countEntries(product.getDescription(), query) > 0)
                .collect(Collectors.toList());
    }

    private List<Product> filterProductsByQueryWithAllMatchingWords(List<Product> products, String query) {
        return products
                .stream()
                .filter(product -> allEntriesFound(product.getDescription(), query))
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
