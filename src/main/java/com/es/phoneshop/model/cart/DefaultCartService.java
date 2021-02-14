package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class DefaultCartService implements  CartService{
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";

    private static DefaultCartService instance;

    private DefaultCartService() {
    }

    public static DefaultCartService getInstance() {
        if(instance == null) {
            instance = new DefaultCartService();
        }
        return instance;
    }

    @Override
    public synchronized Cart getCart(HttpServletRequest request) {
        Cart cart = (Cart) request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);
        if(cart == null) {
            request.getSession().setAttribute(CART_SESSION_ATTRIBUTE, cart = new Cart());
        }
        return cart;
    }

    @Override
    public synchronized void add(Cart cart, Product product, int quantity) throws NotEnoughStockException {
        List<CartItem> cartItems = cart.getItems();
        Optional<CartItem> cartItemInCart = cartItems
                .stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findAny();
        int sumOfRequestedQuantities = quantity;
        if (cartItemInCart.isPresent()) {
            sumOfRequestedQuantities += cartItemInCart.get().getQuantity();
        }
        if (product.getStock() < sumOfRequestedQuantities) {
            throw new NotEnoughStockException(product, quantity, product.getStock());
        }
        if (cartItemInCart.isPresent()) {
            cartItemInCart.get().setQuantity(sumOfRequestedQuantities);
        }
        else {
            cart.getItems().add(new CartItem(product, quantity));
        }
    }
}
