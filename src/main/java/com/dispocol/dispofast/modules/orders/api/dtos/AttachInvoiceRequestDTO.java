package com.dispocol.dispofast.modules.orders.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AttachInvoiceRequestDTO {

  @NotBlank(message = "El número de factura es obligatorio")
  @Size(max = 50, message = "El número de factura no puede superar los 50 caracteres")
  private String invoiceNumber;

  @NotBlank(message = "La URL de la factura es obligatoria")
  @Size(max = 500, message = "La URL de la factura no puede superar los 500 caracteres")
  private String invoiceUrl;
}
