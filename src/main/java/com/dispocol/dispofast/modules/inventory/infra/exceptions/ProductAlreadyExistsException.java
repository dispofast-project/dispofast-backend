package com.dispocol.dispofast.modules.inventory.infra.exceptions;

public class ProductAlreadyExistsException extends RuntimeException {
    
    public ProductAlreadyExistsException(String message) {
        super(message);
    }
    
}
