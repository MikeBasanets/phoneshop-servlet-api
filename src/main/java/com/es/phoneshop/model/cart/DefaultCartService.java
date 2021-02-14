package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

public class DefaultCartService implements  CartService{
    private static DefaultCartService instance;

    private Cart cart = new Cart();
    private ProductDao productDao;

    private DefaultCartService() {
        productDao = ArrayListProductDao.getInstance();
    }

    public static DefaultCartService getInstance() {
        if(instance == null) {
            instance = new DefaultCartService();
        }
        return instance;
    }

    @Override
    public Cart getCart() {
        return cart;
    }

    @Override
    public void add(Long productId, int quantity) throws NotEnoughStockException{
        Product product = productDao.getProduct(productId);
        if(product.getStock() < quantity) {
            throw new NotEnoughStockException(product, quantity, product.getStock());
        }
        cart.getItems().add(new CartItem(product, quantity));
    }
}
