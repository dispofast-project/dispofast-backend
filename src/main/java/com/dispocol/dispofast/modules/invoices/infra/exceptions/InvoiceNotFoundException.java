package com.dispocol.dispofast.modules.invoices.infra.exceptions;

public class InvoiceNotFoundException extends RuntimeException {

  public InvoiceNotFoundException(String message) {
    super(message);
  }
}
