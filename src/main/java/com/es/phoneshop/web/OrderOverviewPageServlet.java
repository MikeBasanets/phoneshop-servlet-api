package com.es.phoneshop.web;

import com.es.phoneshop.model.order.OrderDao;
import com.es.phoneshop.model.order.ArrayListOrderDao;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class OrderOverviewPageServlet extends HttpServlet {
    private static final String PAGE_PATH = "/WEB-INF/pages/orderOverview.jsp";
    private static final String ORDER_ATTRIBUTE = "order";

    private OrderDao orderDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderDao = ArrayListOrderDao.getInstance();
    }

    public void init(ServletConfig config, OrderDao orderDao) throws ServletException {
        super.init(config);
        this.orderDao = orderDao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String secureId = request.getPathInfo().substring(1);
        request.setAttribute(ORDER_ATTRIBUTE, orderDao.getOrderBySecureId(secureId));
        request.getRequestDispatcher(PAGE_PATH).forward(request, response);
    }
}
