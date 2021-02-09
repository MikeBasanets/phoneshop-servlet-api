package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;

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

    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
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
        request.setAttribute(ATTRIBUTE_NAME, productDao.findProducts(query, sortField, sortOrder));
        request.getRequestDispatcher(PAGE_PATH).forward(request, response);
    }
}
