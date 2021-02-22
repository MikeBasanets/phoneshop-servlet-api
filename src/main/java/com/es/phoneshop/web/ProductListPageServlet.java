package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
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

public class ProductListPageServlet extends HttpServlet {
    private static final String ATTRIBUTE_NAME = "products";
    private static final String PAGE_PATH = "/WEB-INF/pages/productList.jsp";
    private static final String QUERY_PARAMETER_NAME = "query";
    private static final String SORT_FIELD_PARAMETER_NAME = "sort";
    private static final String SORT_ORDER_PARAMETER_NAME = "order";
    private static final String RECENTLY_VIEWED_ATTRIBUTE_NAME = "recentlyViewedProducts";

    private RecentlyViewedProductsService recentlyViewedProductsService = DefaultRecentlyViewedProductsService.getInstance();
    private ProductSearchEngine searchEngine;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        searchEngine = new ProductSearchEngine(ArrayListProductDao.getInstance());
    }

    public void init(ServletConfig config, ProductSearchEngine productSearchEngine, RecentlyViewedProductsService recentlyViewedProductsService) throws ServletException {
        super.init(config);
        this.searchEngine = productSearchEngine;
        this.recentlyViewedProductsService = recentlyViewedProductsService;
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
        request.setAttribute(ATTRIBUTE_NAME,  searchEngine.findProducts(query, sortField, sortOrder));
        request.getRequestDispatcher(PAGE_PATH).forward(request, response);
    }

    private void addRecentProducts(HttpServletRequest request) {
        RecentlyViewedProducts recentlyViewedProducts = recentlyViewedProductsService.getRecentlyViewedProducts(request);
        request.setAttribute(RECENTLY_VIEWED_ATTRIBUTE_NAME, recentlyViewedProducts.getItems());
    }
}
