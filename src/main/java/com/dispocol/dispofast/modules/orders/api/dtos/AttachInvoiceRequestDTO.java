package com.dispocol.dispofast.modules.orders.api.dtos;

import lombok.Data;

@Data
public class AttachInvoiceRequestDTO {

  private String invoiceNumber;
  private String invoiceUrl;
}
