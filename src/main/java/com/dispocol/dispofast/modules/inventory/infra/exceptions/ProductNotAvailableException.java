package com.dispocol.dispofast.modules.inventory.infra.exceptions;

public class ProductNotAvailableException extends RuntimeException {
    
    public ProductNotAvailableException(String message) {
        super(message);
    }
    
}
