package com.dispocol.dispofast.modules.orders.api.dtos;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateSalesOrderRequestDTO {

  private String orderNumber;
  private UUID accountId;
  private UUID asesorUserId;
  private OffsetDateTime orderDate;
  private String shipmentCityId;
  private String shipmentAddress;
  private String zone;
  private UUID accountPriceListId;
  private UUID userId;
  private UUID quoteId;
  private List<CreateSalesOrderItemDTO> items;
}
