package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.SampleProducts;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DemoDataServletContextListener implements ServletContextListener {
    private ProductDao productDao;

    public DemoDataServletContextListener() {
        productDao = ArrayListProductDao.getInstance();
    }

    public DemoDataServletContextListener(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        productDao.setProducts(SampleProducts.PRODUCTS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
