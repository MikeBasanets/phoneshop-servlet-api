package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductSearchEngine;
import com.es.phoneshop.model.product.SearchQueryType;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;

public class AdvancedSearchPageServlet extends HttpServlet {
    private static final String PRODUCTS_ATTRIBUTE_NAME = "products";
    private static final String PAGE_PATH = "/WEB-INF/pages/advancedSearch.jsp";
    private static final String QUERY_PARAMETER_NAME = "query";
    private static final String QUERY_TYPE_PARAMETER_NAME = "queryType";
    private static final String QUERY_TYPES_ATTRIBUTE_NAME = "queryTypes";
    private static final String MIN_PRICE_PARAMETER_NAME = "minPrice";
    private static final String MAX_PRICE_PARAMETER_NAME = "maxPrice";

    private ProductSearchEngine searchEngine;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        searchEngine = new ProductSearchEngine(ArrayListProductDao.getInstance());
    }

    public void init(ServletConfig config, ProductSearchEngine productSearchEngine) throws ServletException {
        super.init(config);
        this.searchEngine = productSearchEngine;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter(QUERY_PARAMETER_NAME);
        String searchQueryTypeInput = request.getParameter(QUERY_TYPE_PARAMETER_NAME);
        SearchQueryType queryType = null;
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        try {
            queryType = SearchQueryType.valueOf(searchQueryTypeInput);
        }
        catch (IllegalArgumentException | NullPointerException exception) {
        }
        try {
            minPrice = BigDecimal.valueOf(NumberFormat
                    .getInstance(request.getLocale())
                    .parse(request.getParameter(MIN_PRICE_PARAMETER_NAME))
                    .intValue());
        }
        catch (ParseException | NullPointerException exception) {
        }
        try {
            maxPrice = BigDecimal.valueOf(NumberFormat
                    .getInstance(request.getLocale())
                    .parse(request.getParameter(MAX_PRICE_PARAMETER_NAME))
                    .intValue());
        }
        catch (ParseException | NullPointerException exception) {
        }
        request.setAttribute(QUERY_TYPES_ATTRIBUTE_NAME, Arrays.asList(SearchQueryType.values()));
        request.setAttribute(PRODUCTS_ATTRIBUTE_NAME,  searchEngine.findProducts(query, queryType, minPrice, maxPrice));
        request.getRequestDispatcher(PAGE_PATH).forward(request, response);
    }
}
