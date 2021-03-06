package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class DefaultCartService implements  CartService {
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private static final Currency DEFAULT_CURRENCY = Currency.getInstance(Locale.US);

    private static class SingletonHolder {
        private static final DefaultCartService INSTANCE = new DefaultCartService();
    }

    private DefaultCartService() {
    }

    public static DefaultCartService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public synchronized Cart getCart(HttpServletRequest request) {
        Cart cart = (Cart) request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);
        if(cart == null) {
            request.getSession().setAttribute(CART_SESSION_ATTRIBUTE, cart = new Cart(DEFAULT_CURRENCY));
        }
        return cart;
    }

    @Override
    public void add(Cart cart, Product product, int quantity) throws NotEnoughStockException {
        synchronized (cart) {
            List<CartItem> cartItems = cart.getItems();
            Optional<CartItem> cartItemInCart = findCartItemToUpdate(cartItems, product);
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
                cartItems.add(new CartItem(product, quantity));
            }
            recalculateCart(cart);
        }
    }

    @Override
    public void update(Cart cart, Product product, int quantity) throws NotEnoughStockException {
        synchronized (cart) {
            List<CartItem> cartItems = cart.getItems();
            if(quantity == 0) {
                delete(cart, product);
                return;
            }
            Optional<CartItem> cartItemInCart = findCartItemToUpdate(cartItems, product);
            if (product.getStock() < quantity) {
                throw new NotEnoughStockException(product, quantity, product.getStock());
            }
            if (cartItemInCart.isPresent()) {
                cartItemInCart.get().setQuantity(quantity);
            }
            else {
                cartItems.add(new CartItem(product, quantity));
            }
            recalculateCart(cart);
        }
    }

    @Override
    public void delete(Cart cart, Product product) {
        synchronized (cart) {
            cart.getItems().removeIf(item ->
                    item.getProduct().getId().equals(product.getId())
            );
            recalculateCart(cart);
        }
    }

    private Optional<CartItem> findCartItemToUpdate(List<CartItem> cartItems, Product product) {
        return cartItems
                .stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findAny();
    }

    private void recalculateCart(Cart cart) {
        cart.setTotalQuantity(cart.getItems()
                .stream()
                .map(CartItem::getQuantity)
                .mapToInt(quantity -> quantity).sum()
        );
        cart.setTotalCost(cart.getItems()
                .stream()
                .map(cartItem -> cartItem
                        .getProduct()
                        .getPrice()
                        .multiply(new BigDecimal(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }
}
