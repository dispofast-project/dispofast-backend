package com.dispocol.dispofast.modules.cartera.api.dtos;

import com.dispocol.dispofast.modules.cartera.domain.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Data;

/** Un solo pago del cliente repartido entre varias de sus facturas pendientes. */
@Data
public class CreateMultiInvoicePaymentRequestDTO {

  @NotNull(message = "El cliente es obligatorio")
  private UUID clientId;

  @NotNull(message = "La fecha de pago es obligatoria")
  private LocalDate paymentDate;

  @NotNull(message = "El método de pago es obligatorio")
  private PaymentMethod paymentMethod;

  private String documentNumber;

  private String voucherS3Key;

  private String observations;

  @NotEmpty(message = "Debe seleccionar al menos una factura")
  @Valid
  private List<PaymentAllocationRequestDTO> allocations;
}
