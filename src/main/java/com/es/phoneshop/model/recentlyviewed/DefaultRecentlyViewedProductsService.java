package com.es.phoneshop.model.recentlyviewed;

import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class DefaultRecentlyViewedProductsService implements RecentlyViewedProductsService {
    private static final String RECENTLY_VIEWED_SESSION_ATTRIBUTE = DefaultRecentlyViewedProductsService.class.getName() + ".recentlyViewed";
    private static final int MAX_ITEMS_IN_RECENTLY_VIEWED_LIST = 3;

    private static class SingletonHolder {
        private static final DefaultRecentlyViewedProductsService INSTANCE = new DefaultRecentlyViewedProductsService();
    }

    private DefaultRecentlyViewedProductsService() {
    }

    public static DefaultRecentlyViewedProductsService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public synchronized RecentlyViewedProducts getRecentlyViewedProducts(HttpServletRequest request) {
        HttpSession session = request.getSession();
        RecentlyViewedProducts recentlyViewedProducts = (RecentlyViewedProducts) session
                .getAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE);
        if(recentlyViewedProducts == null) {
            recentlyViewedProducts = new RecentlyViewedProducts();
            session.setAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE, recentlyViewedProducts);
        }
        return  recentlyViewedProducts;
    }

    @Override
    public synchronized void addProduct(Product product, RecentlyViewedProducts recentlyViewedProducts) {
        List<Product> recentProductsList = recentlyViewedProducts.getItems();
        recentProductsList.removeIf(productInList -> product.getId().equals(productInList.getId()));
        recentProductsList.add(0, product);
        if(recentProductsList.size() > MAX_ITEMS_IN_RECENTLY_VIEWED_LIST) {
            recentProductsList.remove(MAX_ITEMS_IN_RECENTLY_VIEWED_LIST);
        }
    }
}
