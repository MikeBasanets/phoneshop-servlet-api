package com.es.phoneshop.model.product;

import java.util.List;

public interface ProductDao {
    List<Product> getProducts();
    void setProducts(List<Product> products);
    Product getProduct(Long id) throws NoSuchProductException;
    void save(Product product);
    void delete(Long id);
}
