package com.dispocol.dispofast.modules.shipping.api.dtos;

import com.dispocol.dispofast.shared.location.api.dto.CityDTO;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentResponseDTO {

  private UUID id;
  private UUID invoiceId;
  private String invoiceNumber;
  private OffsetDateTime createdAt;
  private String clientName;
  private String asesorName;
  private String state;
  private String stateLabel;
  private String deliveryAddress;
  private String addressDetail;
  private OffsetDateTime departureDate;
  private OffsetDateTime deliveryDate;
  private CarrierResponseDTO carrier;
  private DriverResponseDTO driver;
  private CityDTO city;
  private LocalDate estimatedDeliveryDate;
  private Integer productCount;
  private String deliveryType;
  private String trackingCode;
  private List<ShipmentItemResponseDTO> items;
}
