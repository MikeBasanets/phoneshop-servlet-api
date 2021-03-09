package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.cart.NotEnoughStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.recentlyviewed.DefaultRecentlyViewedProductsService;
import com.es.phoneshop.model.recentlyviewed.RecentlyViewedProducts;
import com.es.phoneshop.model.recentlyviewed.RecentlyViewedProductsService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

public class ProductDetailsPageServlet extends HttpServlet {
    private static final String PRODUCT_ATTRIBUTE_NAME = "product";
    private static final String ERROR_ATTRIBUTE_NAME = "error";
    private static final String SERVLET_RELATIVE_PATH_MAPPING = "/products/";
    private static final String QUANTITY_PARAMETER_NAME = "quantity";
    private static final String REDIRECT_MESSAGE_PARAMETER_NAME = "message";
    private static final String ADDED_TO_CART_MESSAGE = "Product added to cart";
    private static final String NUMBER_NOT_VALID_MESSAGE = "Not a valid product quantity";
    private static final String NOT_ENOUGH_STOCK_MESSAGE = "Not enough stock, available ";
    private static final String PAGE_PATH = "/WEB-INF/pages/product.jsp";

    private ProductDao productDao;
    private CartService cartService;
    private RecentlyViewedProductsService recentlyViewedProductsService;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        recentlyViewedProductsService = DefaultRecentlyViewedProductsService.getInstance();
        cartService = DefaultCartService.getInstance();
    }

    public void init(ServletConfig config, ProductDao productDao, CartService cartService,
                     RecentlyViewedProductsService recentlyViewedProductsService) throws ServletException {
        super.init(config);
        this.productDao = productDao;
        this.cartService = cartService;
        this.recentlyViewedProductsService = recentlyViewedProductsService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = Long.valueOf(request.getPathInfo().substring(1));
        RecentlyViewedProducts recentlyViewedProducts = recentlyViewedProductsService.getRecentlyViewedProducts(request);
        recentlyViewedProductsService.addProduct(productDao.getProduct(productId), recentlyViewedProducts);
        request.setAttribute(ServletConstants.RECENTLY_VIEWED_ATTRIBUTE_NAME, recentlyViewedProducts.getItems());
        request.setAttribute(PRODUCT_ATTRIBUTE_NAME, productDao.getProduct(productId));
        request.getRequestDispatcher(PAGE_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = Long.valueOf(request.getPathInfo().substring(1));
        String quantityString = request.getParameter(QUANTITY_PARAMETER_NAME);
        int quantity;
        try {
            quantity = NumberFormat.getInstance(request.getLocale()).parse(quantityString).intValue();
        }
        catch (ParseException exception) {
            request.setAttribute(ERROR_ATTRIBUTE_NAME, NUMBER_NOT_VALID_MESSAGE);
            doGet(request, response);
            return;
        }
        if(quantity <= 0) {
            request.setAttribute(ERROR_ATTRIBUTE_NAME, NUMBER_NOT_VALID_MESSAGE);
            doGet(request, response);
            return;
        }
        try {
            cartService.add(cartService.getCart(request), productDao.getProduct(productId), quantity);
        }
        catch (NotEnoughStockException exception) {
            request.setAttribute(ERROR_ATTRIBUTE_NAME, NOT_ENOUGH_STOCK_MESSAGE + exception.getStockAvailable());
            doGet(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + SERVLET_RELATIVE_PATH_MAPPING + productId + "?" + REDIRECT_MESSAGE_PARAMETER_NAME + "=" + ADDED_TO_CART_MESSAGE);
    }
}
