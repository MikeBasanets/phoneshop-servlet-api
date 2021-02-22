package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteCartItemServlet extends HttpServlet {
    private static final String SUCCESSFUL_REDIRECT_RELATIVE_URL = "/cart?message=Cart item removed successfully";

    private CartService cartService;
    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
        productDao = ArrayListProductDao.getInstance();
    }

    public void init(ServletConfig config, CartService cartService, ProductDao productDao) throws ServletException {
        super.init(config);
        this.cartService = cartService;
        this.productDao = productDao;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Product product = productDao.getProduct(Long.valueOf(request.getPathInfo().substring(1)));
        Cart cart = cartService.getCart(request);
        cartService.delete(cart, product);
        response.sendRedirect(request.getContextPath() + SUCCESSFUL_REDIRECT_RELATIVE_URL);
    }
}
