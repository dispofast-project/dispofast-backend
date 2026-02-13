package com.dispocol.dispofast.modules.inventory.infra.exceptions;

public class ProductNotFoundException extends RuntimeException {
    
    public ProductNotFoundException(String message) {
        super(message);
    }
    
}
