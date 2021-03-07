package com.es.phoneshop.model.order;

public class NoSuchOrderException extends RuntimeException {
    public NoSuchOrderException(String message) {
        super(message);
    }
}
