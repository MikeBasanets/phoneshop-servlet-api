package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.cart.NotEnoughStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.ProductSearchEngine;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;
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
import java.util.HashMap;
import java.util.Map;

public class ProductListPageServlet extends HttpServlet {
    private static final String PRODUCTS_ATTRIBUTE_NAME = "products";
    private static final String PAGE_PATH = "/WEB-INF/pages/productList.jsp";
    private static final String QUERY_PARAMETER_NAME = "query";
    private static final String SORT_FIELD_PARAMETER_NAME = "sort";
    private static final String SORT_ORDER_PARAMETER_NAME = "order";
    private static final String ERROR_ATTRIBUTE_NAME = "errors";
    private static final String NUMBER_NOT_VALID_MESSAGE = "Not a valid product quantity";
    private static final String NOT_ENOUGH_STOCK_MESSAGE = "Not enough stock, available ";
    private static final String PRODUCT_ID_PARAMETER_NAME = "productId";
    private static final String PRODUCT_QUANTITY_PARAMETER_NAME = "quantity";
    private static final String SUCCESSFUL_REDIRECT_RELATIVE_URL =  "/products?message=Product added to cart successfully";


    private RecentlyViewedProductsService recentlyViewedProductsService = DefaultRecentlyViewedProductsService.getInstance();
    private ProductSearchEngine searchEngine;
    private CartService cartService;
    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        searchEngine = new ProductSearchEngine(ArrayListProductDao.getInstance());
        cartService = DefaultCartService.getInstance();
        productDao = ArrayListProductDao.getInstance();
    }

    public void init(ServletConfig config, ProductSearchEngine productSearchEngine,
                     RecentlyViewedProductsService recentlyViewedProductsService,
                     CartService cartService, ProductDao productDao) throws ServletException {
        super.init(config);
        this.searchEngine = productSearchEngine;
        this.recentlyViewedProductsService = recentlyViewedProductsService;
        this.cartService = cartService;
        this.productDao = productDao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter(QUERY_PARAMETER_NAME);
        String sortFieldInput = request.getParameter(SORT_FIELD_PARAMETER_NAME);
        String sortOrderInput = request.getParameter(SORT_ORDER_PARAMETER_NAME);
        SortField sortField = null;
        SortOrder sortOrder = null;
        try {
            sortField = SortField.valueOf(sortFieldInput);
        }
        catch (IllegalArgumentException | NullPointerException exception) {
        }
        try {
            sortOrder = SortOrder.valueOf(sortOrderInput);
        }
        catch (IllegalArgumentException | NullPointerException exception) {
        }
        addRecentProducts(request);
        request.setAttribute(PRODUCTS_ATTRIBUTE_NAME,  searchEngine.findProducts(query, sortField, sortOrder));
        request.getRequestDispatcher(PAGE_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<Long, String> errors = addToCart(request);
        if(errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + SUCCESSFUL_REDIRECT_RELATIVE_URL);
        }
        else {
            request.setAttribute(ERROR_ATTRIBUTE_NAME, errors);
            doGet(request, response);
        }
    }

    private Map<Long, String> addToCart(HttpServletRequest request) {
        Long productId = Long.valueOf(request.getParameter(PRODUCT_ID_PARAMETER_NAME));
        String quantityString = request.getParameter(PRODUCT_QUANTITY_PARAMETER_NAME);
        Map<Long, String> errors = new HashMap<>();
        int quantity;
        try {
            quantity = NumberFormat.getInstance(request.getLocale()).parse(quantityString).intValue();
        }
        catch (ParseException exception) {
            errors.put(productId, NUMBER_NOT_VALID_MESSAGE);
            return errors;
        }
        if(quantity < 1) {
            errors.put(productId, NUMBER_NOT_VALID_MESSAGE);
            return errors;
        }
        try {
            cartService.add(cartService.getCart(request), productDao.getProduct(productId), quantity);
        }
        catch (NotEnoughStockException exception) {
            errors.put(productId, NOT_ENOUGH_STOCK_MESSAGE + productDao.getProduct(productId).getStock());
        }
        return errors;
    }

    private void addRecentProducts(HttpServletRequest request) {
        RecentlyViewedProducts recentlyViewedProducts = recentlyViewedProductsService.getRecentlyViewedProducts(request);
        request.setAttribute(ServletConstants.RECENTLY_VIEWED_ATTRIBUTE_NAME, recentlyViewedProducts.getItems());
    }
}
