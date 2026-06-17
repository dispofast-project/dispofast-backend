package com.dispocol.dispofast.modules.shipping.api.dtos;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateShipmentDTO {

  private String deliveryAddress;
  private UUID carrierId;
  private String cityCode;
  private LocalDate estimatedDeliveryDate;
}
