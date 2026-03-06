package com.dispocol.dispofast.modules.orders.api.dtos;

import com.dispocol.dispofast.modules.orders.domain.OrderState;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class UpdateSalesOrderRequestDTO {

  private UUID asesorUserId;
  private OrderState state;
  private OffsetDateTime orderDate;
  private String shipmentCityId;
  private String shipmentAddress;
  private String zone;
  private UUID accountPriceListId;
  private List<CreateSalesOrderItemDTO> items;
}
