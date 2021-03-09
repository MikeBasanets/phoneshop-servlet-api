package com.es.phoneshop.web;

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

public class PriceHistoryPageServlet extends HttpServlet {
    private static final String ATTRIBUTE_NAME = "product";
    private static final String PAGE_PATH = "/WEB-INF/pages/priceHistory.jsp";

    private RecentlyViewedProductsService recentlyViewedProductsService = DefaultRecentlyViewedProductsService.getInstance();
    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        recentlyViewedProductsService = DefaultRecentlyViewedProductsService.getInstance();
    }

    public void init(ServletConfig config, ProductDao productDao, RecentlyViewedProductsService recentlyViewedProductsService) throws ServletException {
        super.init(config);
        this.productDao = productDao;
        this.recentlyViewedProductsService = recentlyViewedProductsService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = Long.valueOf(request.getPathInfo().substring(1));
        request.setAttribute(ATTRIBUTE_NAME, productDao.getProduct(productId));
        addRecentProducts(request);
        request.getRequestDispatcher(PAGE_PATH).forward(request, response);
    }

    private void addRecentProducts(HttpServletRequest request) {
        RecentlyViewedProducts recentlyViewedProducts = recentlyViewedProductsService.getRecentlyViewedProducts(request);
        request.setAttribute(ServletConstants.RECENTLY_VIEWED_ATTRIBUTE_NAME, recentlyViewedProducts.getItems());
    }
}
