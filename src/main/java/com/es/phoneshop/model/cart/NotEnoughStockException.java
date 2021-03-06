package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;

public class NotEnoughStockException extends Exception {
    private Product product;
    private int stockRequested;
    private int stockAvailable;

    public NotEnoughStockException(Product product, int stockRequested, int stockAvailable) {
        this.product = product;
        this.stockRequested = stockRequested;
        this.stockAvailable = stockAvailable;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getStockRequested() {
        return stockRequested;
    }

    public void setStockRequested(int stockRequested) {
        this.stockRequested = stockRequested;
    }

    public int getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(int stockAvailable) {
        this.stockAvailable = stockAvailable;
    }
}
