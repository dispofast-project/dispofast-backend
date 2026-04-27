package com.dispocol.dispofast.modules.invoices.domain;

public enum InvoiceState {
  ISSUED,
  VOID;

  public String getValue() {
    return name();
  }
}
