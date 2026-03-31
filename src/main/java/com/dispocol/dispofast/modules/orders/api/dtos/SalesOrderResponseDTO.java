package com.dispocol.dispofast.modules.orders.api.dtos;

import com.dispocol.dispofast.modules.orders.domain.OrderState;
import com.dispocol.dispofast.shared.location.domain.LocationZone;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class SalesOrderResponseDTO {

  private UUID id;
  private String orderNumber;
  private UUID clientId;
  private String clientName;
  private UUID asesorUserId;
  private String asesorName;
  private OrderState state;
  private OffsetDateTime orderDate;
  private String shipmentCityId;
  private String shipmentCityName;
  private String shipmentAddress;
  private LocationZone zone;
  private BigDecimal totalValue;
  private UUID priceListId;
  private UUID quoteId;
  private List<SalesOrderItemResponseDTO> items;
}
