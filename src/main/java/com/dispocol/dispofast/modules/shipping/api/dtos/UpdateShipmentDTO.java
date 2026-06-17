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

  private String deliveryType;
  private String deliveryAddress;
  private String addressDetail;
  private UUID carrierId;
  private UUID driverId;
  private String cityCode;
  private LocalDate estimatedDeliveryDate;
  private String trackingCode;
}
