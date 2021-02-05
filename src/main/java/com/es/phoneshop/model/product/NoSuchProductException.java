package com.es.phoneshop.model.product;

public class NoSuchProductException extends Exception {
    public NoSuchProductException(String message) {
        super(message);
    }
}
