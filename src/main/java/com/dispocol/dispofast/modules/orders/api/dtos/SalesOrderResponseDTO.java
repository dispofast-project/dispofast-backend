package com.dispocol.dispofast.modules.orders.api.dtos;

import com.dispocol.dispofast.modules.orders.domain.OrderState;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class SalesOrderResponseDTO {

  private UUID id;
  private String orderNumber;
  private UUID accountId;
  private String accountName;
  private UUID asesorUserId;
  private String asesorName;
  private OrderState state;
  private OffsetDateTime orderDate;
  private String shipmentCityId;
  private String shipmentCityName;
  private String shipmentAddress;
  private String zone;
  private BigDecimal totalValue;
  private UUID priceListId;
  private UUID quoteId;
  private String invoiceNumber;
  private String invoiceUrl;
  private List<SalesOrderItemResponseDTO> items;
}
