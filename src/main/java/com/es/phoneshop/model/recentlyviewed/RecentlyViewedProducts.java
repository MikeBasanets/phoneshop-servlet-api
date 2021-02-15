package com.es.phoneshop.model.recentlyviewed;

import com.es.phoneshop.model.product.Product;

import java.util.ArrayList;
import java.util.List;

public class RecentlyViewedProducts {

    private List<Product> items = new ArrayList<>();

    public RecentlyViewedProducts() {
    }

    public RecentlyViewedProducts(List<Product> items) {
        this.items = items;
    }

    public List<Product> getItems() {
        return items;
    }

    public void setItems(List<Product> items) {
        this.items = items;
    }
}
