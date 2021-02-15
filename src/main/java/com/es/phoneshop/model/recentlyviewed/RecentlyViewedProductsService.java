package com.es.phoneshop.model.recentlyviewed;

import javax.servlet.http.HttpServletRequest;

public interface RecentlyViewedProductsService {
    RecentlyViewedProducts getRecentlyViewedProducts(HttpServletRequest request);
    void addProduct(Long productId, RecentlyViewedProducts recentlyViewedProducts);
}
