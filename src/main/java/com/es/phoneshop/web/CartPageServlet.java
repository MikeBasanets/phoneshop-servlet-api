package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.cart.NotEnoughStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class CartPageServlet extends HttpServlet {
    private static final String CART_ATTRIBUTE_NAME = "cart";
    private static final String PRODUCT_ID_PARAMETER_NAME = "productId";
    private static final String PRODUCT_QUANTITY_PARAMETER_NAME = "quantity";
    private static final String PAGE_PATH = "/WEB-INF/pages/cart.jsp";
    private static final String ERROR_ATTRIBUTE_NAME = "errors";
    private static final String SUCCESSFUL_REDIRECT_RELATIVE_URL =  "/cart?message=Cart updated successfully";
    private static final String NUMBER_NOT_VALID_MESSAGE = "Not a valid product quantity";
    private static final String NOT_ENOUGH_STOCK_MESSAGE = "Not enough stock, available ";

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(CART_ATTRIBUTE_NAME, cartService.getCart(request));
        request.getRequestDispatcher(PAGE_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<Long, String> errors = applyUpdates(request);
        if(errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + SUCCESSFUL_REDIRECT_RELATIVE_URL);
        }
        else {
            request.setAttribute(ERROR_ATTRIBUTE_NAME, errors);
            doGet(request, response);
        }
    }

    private Map<Long, String> applyUpdates(HttpServletRequest request) {
        String[] productIds = request.getParameterValues(PRODUCT_ID_PARAMETER_NAME);
        String[] quantities = request.getParameterValues(PRODUCT_QUANTITY_PARAMETER_NAME);
        Map<Long, String> errors = new HashMap<>();
        if(productIds == null) {
            return errors;
        }
        for(int i = 0; i < productIds.length; i++) {
            Long productId = Long.valueOf(productIds[i]);
            int quantity;
            try {
                quantity = NumberFormat.getInstance(request.getLocale()).parse(quantities[i]).intValue();
            }
            catch (ParseException exception) {
                errors.put(productId, NUMBER_NOT_VALID_MESSAGE);
                continue;
            }
            if(quantity < 0) {
                errors.put(productId, NUMBER_NOT_VALID_MESSAGE);
                continue;
            }
            try {
                cartService.update(cartService.getCart(request), productDao.getProduct(productId), quantity);
            }
            catch (NotEnoughStockException exception) {
                errors.put(productId, NOT_ENOUGH_STOCK_MESSAGE + productDao.getProduct(productId).getStock());
            }
        }
        return errors;
    }
}
