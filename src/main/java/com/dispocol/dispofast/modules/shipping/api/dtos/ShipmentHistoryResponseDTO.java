package com.dispocol.dispofast.modules.shipping.api.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentHistoryResponseDTO {

  private UUID id;
  private OffsetDateTime changedAt;
  private String description;
  private String userEmail;
}
